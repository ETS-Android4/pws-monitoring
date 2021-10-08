package pws.monitoring.feri.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

public class RecipientModifyFragment extends Fragment {
    private EditText edtMMoisture;
    private EditText edtMModifier;
    private EditText edtFrequency;
    private EditText edtFModifier;
    private EditText edtByteAddress;
    private EditText edtRelayPin;
    private EditText edtMoisturePin;
    private Button buttonChangePlant;
    private Button buttonUpdateRecipient;
    private Button buttonDeleteRecipient;
    private Button buttonUpdatePlant;

    private User user;
    private Recipient recipient;
    private Plant scannedPlant;

    private CompositeSubscription subscription;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_recipient_modify, container, false);

        user = ApplicationState.loadLoggedUser();
        subscription = new CompositeSubscription();

        Bundle bundle = getArguments();
        recipient = ApplicationState.getGson().fromJson(bundle.getString("recipient"),
                Recipient.class);

        bindGUI(rootView);
        bindValues();

        return rootView;
    }

    private void bindValues() {
        edtMMoisture.setHint(String.valueOf(recipient.getPlant().getMoisture()));
        edtMModifier.setHint(String.valueOf(recipient.getPlant().getMoistureModifier()));
        edtFrequency.setHint(String.valueOf(recipient.getPlant().getFrequency()));
        edtFModifier.setHint(String.valueOf(recipient.getPlant().getFrequencyModifier()));
        edtByteAddress.setHint(recipient.getByteAddress());
        edtRelayPin.setHint(String.valueOf(recipient.getRelayPin()));
    }

    private void bindGUI(ViewGroup v) {
        edtMMoisture = (EditText) v.findViewById(R.id.edtMMoisture);
        edtMModifier = (EditText) v.findViewById(R.id.edtMModifier);
        edtFrequency = (EditText) v.findViewById(R.id.edtFrequency);
        edtFModifier = (EditText) v.findViewById(R.id.edtFModifier);
        edtByteAddress = (EditText) v.findViewById(R.id.edtByteAddress);
        edtRelayPin = (EditText) v.findViewById(R.id.edtRelayPin);
        edtMoisturePin = (EditText) v.findViewById(R.id.edtMoisturePin);

        buttonChangePlant = (Button) v.findViewById(R.id.buttonChangePlant);
        buttonChangePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQrScan();
            }
        });

        buttonUpdateRecipient = (Button) v.findViewById(R.id.buttonUpdateRecipient);
        buttonUpdateRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edtByteAddress.getText().toString().equals(""))
                    recipient.setByteAddress(edtByteAddress.getText().toString());

                if(!edtRelayPin.getText().toString().equals(""))
                    recipient.setRelayPin(Integer.parseInt(edtRelayPin.getText().toString()));

                if(!edtMoisturePin.getText().toString().equals(""))
                    recipient.setMoisturePin(Integer.parseInt(edtMoisturePin.getText().toString()));

                if(scannedPlant != null)
                    recipient.setPlant(scannedPlant);

                handleApiRequest("updateRecipient");
            }
        });


        buttonUpdatePlant = (Button) v.findViewById(R.id.buttonUpdatePlant);
        buttonUpdatePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edtMMoisture.getText().toString().equals(""))
                    recipient.getPlant().setMoisture(Integer.parseInt(edtMMoisture.getText().toString()));

                if(!edtMModifier.getText().toString().equals(""))
                    recipient.getPlant().setMoistureModifier(Integer.parseInt(edtMModifier.getText().toString()));

                if(!edtFrequency.getText().toString().equals(""))
                    recipient.getPlant().setFrequency(Integer.parseInt(edtFrequency.getText().toString()));

                if(!edtFModifier.getText().toString().equals(""))
                    recipient.getPlant().setFrequencyModifier(Integer.parseInt(edtFModifier.getText().toString()));

                handleApiRequest("updatePlant");
            }

        });

        buttonDeleteRecipient = (Button) v.findViewById(R.id.buttonDeleteRecipient);
        buttonDeleteRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleApiRequest("deleteRecipient");
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
                scannedPlant = ApplicationState.getGson().fromJson(result.getContents(), Plant.class);

            }
        }
    }

    private void handleApiRequest(String type){
        switch (type){
            case "updatePlant":
                subscription.add(NetworkUtil.getRetrofit().updatePlant(user.getId(), recipient.getId(),
                        recipient.getPlant().getId(), recipient.getPlant())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                break;
            case "updateRecipient":
                subscription.add(NetworkUtil.getRetrofit().updateRecipient(user.getId(), recipient.getId(), recipient)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                break;
            case "deleteRecipient":
                subscription.add(NetworkUtil.getRetrofit().removeRecipient(user.getId(), recipient.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                break;
        }
    }

    private void handleResponse(User user) {
        ApplicationState.saveLoggedUser(user);
        EventBus.getDefault().post(new OnUserUpdated(user));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        RecipientListFragment recipientListFragment = new RecipientListFragment();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_navigation,
                recipientListFragment).commit();
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
    }

    private void startQrScan(){
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(RecipientModifyFragment.this);

        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan QR code");
        integrator.setBeepEnabled(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);


        integrator.initiateScan();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
