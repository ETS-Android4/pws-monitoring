package pws.monitoring.feri.modals;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.regex.Pattern;

import pws.monitoring.datalib.Plant;
import pws.monitoring.datalib.Recipient;
import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.activities.LogInActivity;
import pws.monitoring.feri.events.OnUserUpdated;
import pws.monitoring.feri.network.NetworkError;
import pws.monitoring.feri.network.NetworkUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddRecipientModal extends DialogFragment {
    public static final String TAG = AddRecipientModal.class.getSimpleName();

    public AlertDialog.Builder builder;
    public LayoutInflater inflater;
    public View view;
    public Dialog dialog;

    private EditText edtPin;
    private EditText edtPinMoisture;
    private EditText edtAddress;
    private Button buttonQrScan;

    private CompositeSubscription subscription;

    private User user;

    public static AddRecipientModal newInstance() {
        AddRecipientModal modal = new AddRecipientModal();
        return modal;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.builder = this.builder != null ? this.builder : new AlertDialog.Builder(getActivity());
        this.inflater = this.inflater != null ? this.inflater : getActivity().getLayoutInflater();
        this.view = this.view != null ? this.view : inflater.inflate(R.layout.modal_add_recipient, null);
        builder.setView(view);
        this.dialog = dialog != null ? this.dialog : builder.create();

        bindGUI(view);

        subscription = new CompositeSubscription();
        user = ApplicationState.loadLoggedUser();

        return dialog;
    }

    public void bindGUI(View v) {
        edtAddress = (EditText)v.findViewById(R.id.edtAddress);
        edtPin = (EditText) v.findViewById(R.id.editTextPin);
        edtPinMoisture = (EditText) v.findViewById(R.id.editTextPinMoisture);
        buttonQrScan = (Button)v.findViewById(R.id.buttonQrScan);
        buttonQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edtAddress.getText().toString()) &&
                        TextUtils.isEmpty(edtPin.getText().toString())){
                    Toast.makeText(requireContext(), getResources().getString(R.string.validation_fields_required), Toast.LENGTH_SHORT);
                } else {
                    IntentIntegrator integrator = IntentIntegrator.forSupportFragment(AddRecipientModal.this);

                    integrator.setOrientationLocked(false);
                    integrator.setPrompt(getResources().getString(R.string.text_scan));
                    integrator.setBeepEnabled(false);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

                    integrator.initiateScan();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.text_cancelled), Toast.LENGTH_LONG).show();
            } else if (Recipient.validatePins(edtAddress.getText().toString(),
                    "A" + edtPinMoisture.getText().toString(),
                    Integer.parseInt(edtPin.getText().toString()))){
                Toast.makeText(getContext(), getResources().getString(R.string.text_scanned), Toast.LENGTH_LONG).show();
                Plant plant = ApplicationState.getGson().fromJson(result.getContents(), Plant.class);
                Recipient recipient = new Recipient();
                recipient.setPlant(plant);
                recipient.setByteAddress(edtAddress.getText().toString());
                recipient.setRelayPin(Integer.parseInt(edtPin.getText().toString()));
                recipient.setMoisturePin("A" + edtPinMoisture.getText().toString());

                createRecipient(recipient);
            } else {
                Toast.makeText(requireContext(), R.string.validation_pins, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createRecipient(Recipient recipient) {
        subscription.add(NetworkUtil.getRetrofit().createRecipient(user.getId(), recipient)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(User user) {
        ApplicationState.saveLoggedUser(user);
        EventBus.getDefault().post(new OnUserUpdated(user));
        dismiss();
    }

    private void handleError(Throwable error) {
        Log.e(TAG, error.getMessage());
        NetworkError networkError = new NetworkError(error, getActivity());
        networkError.handleError();
        dismiss();
    }

}
