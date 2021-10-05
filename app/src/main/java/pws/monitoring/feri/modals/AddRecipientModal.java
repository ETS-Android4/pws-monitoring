package pws.monitoring.feri.modals;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

import pws.monitoring.datalib.Plant;
import pws.monitoring.datalib.Recipient;
import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.events.OnUserUpdated;
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

    EditText edtPin;
    EditText edtPinMoisture;
    EditText edtAddress;
    Button buttonQrScan;

    private CompositeSubscription subscription;

    User user;

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
                if(edtAddress.getText().toString().equals("") &&
                        edtPin.getText().toString().equals("")){
                    Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT);
                } else {
                    IntentIntegrator integrator = IntentIntegrator.forSupportFragment(AddRecipientModal.this);

                    integrator.setOrientationLocked(false);
                    integrator.setPrompt("Scan QR code");
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
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Scanned", Toast.LENGTH_LONG).show();
                Plant plant = ApplicationState.getGson().fromJson(result.getContents(), Plant.class);
                Recipient recipient = new Recipient();
                recipient.setPlant(plant);
                recipient.setByteAddress(edtAddress.getText().toString());
                recipient.setRelayPin(Integer.parseInt(edtPin.getText().toString()));
                recipient.setMoisturePin(Integer.parseInt(edtPinMoisture.getText().toString()));

                createRecipient(recipient);
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
        EventBus.getDefault().post(new OnUserUpdated());
        dismiss();
    }

    private void handleError(Throwable error) {

        if (error instanceof HttpException) {
            try {
                String errorBody = ((HttpException) error).response().errorBody().string();
                Log.i("REGISTER ERROR", errorBody);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("REGISTER ERROR", error.getMessage());
        }
        dismiss();
    }


}
