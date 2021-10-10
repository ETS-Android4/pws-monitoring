package pws.monitoring.feri.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import pws.monitoring.datalib.Recipient;
import pws.monitoring.datalib.Request;
import pws.monitoring.datalib.Response;
import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.network.NetworkUtil;
import pws.monitoring.feri.util.MonthUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RecipientDetailsFragment extends Fragment {
    private TextView textViewPlantNames;
    private TextView textViewPlantTechData;
    private TextView rowPLight;
    private TextView rowRLight;
    private TextView rowPHumidity;
    private TextView rowRHumidity;
    private TextView rowPTemperature;
    private TextView rowRTemperature;
    private TextView rowPMoisture;
    private TextView rowRMoisture;
    private TextView rowGrowthMonth;
    private TextView rowWinterMonth;
    private TextView rowGrowthMoisture;
    private TextView rowWinterMoisture;
    private TextView rowGrowingFrequency;
    private TextView rowWinterFrequency;
    private Button buttonRefresh;
    private Button buttonWater;
    private ImageView imageView;

    private CompositeSubscription subscription;

    private User user;
    private Recipient recipient;
    private Response response;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_recipient_details, container, false);

        subscription = new CompositeSubscription();
        user = ApplicationState.loadLoggedUser();
        Bundle bundle = getArguments();
        recipient = ApplicationState.getGson().fromJson(bundle.getString("recipient"),
                Recipient.class);

        bindGUI(rootView);
        bindValues();

        return rootView;
    }

    private void bindValues() {
        textViewPlantNames.setText(recipient.getPlant().getCommonName() + " (" + recipient.getPlant().getLatinName() + ")");
        textViewPlantTechData.setText("Device IP: " + user.getIp() + " on byte address " + recipient.getByteAddress() +
                " and relay/moisture pins " + String.valueOf(recipient.getRelayPin()) + " " +
                String.valueOf(recipient.getMoisturePin()));
        rowPLight.setText(String.valueOf(recipient.getPlant().getLight()));
        rowPHumidity.setText(String.valueOf(recipient.getPlant().getHumidity()));
        rowPTemperature.setText(String.valueOf(recipient.getPlant().getTemperature()));
        rowGrowthMonth.setText(MonthUtil.getMonthName(recipient.getPlant().getGrowthMonth()));
        rowWinterMonth.setText(MonthUtil.getMonthName(recipient.getPlant().getHibernationMonth()));
        rowGrowthMoisture.setText(String.valueOf(recipient.getPlant().getMoisture()));
        rowWinterMoisture.setText(String.valueOf(recipient.getPlant().getMoisture() -
                recipient.getPlant().getMoistureModifier()));
        rowGrowingFrequency.setText(String.valueOf(recipient.getPlant().getFrequency()));
        rowWinterFrequency.setText(String.valueOf(recipient.getPlant().getFrequency() -
                recipient.getPlant().getFrequencyModifier()));

        if(MonthUtil.isWinterSeason(Calendar.getInstance().get(Calendar.MONTH)+1,
                recipient.getPlant().getGrowthMonth(), recipient.getPlant().getHibernationMonth()))
            rowPMoisture.setText(String.valueOf(recipient.getPlant().getMoisture() - recipient.getPlant().getMoistureModifier()));
        else
            rowPMoisture.setText(String.valueOf(recipient.getPlant().getMoisture()));

        rowRLight.setText("Not fetched");
        rowRHumidity.setText("Not fetched");
        rowRTemperature.setText("Not fetched");
        rowRMoisture.setText("Not fetched");

        if(!recipient.getPath().equals("")){
            File imageFile = new File(recipient.getPath());
            Picasso.get().load(imageFile).fit().into(imageView);
        }
    }


    private void bindGUI(ViewGroup v) {
        textViewPlantNames = (TextView) v.findViewById(R.id.textViewPlantNames) ;
        textViewPlantTechData= (TextView) v.findViewById(R.id.textViewPlantTechData) ;
        rowPLight = (TextView) v.findViewById(R.id.rowPLight) ;
        rowRLight = (TextView) v.findViewById(R.id.rowRLight) ;
        rowPHumidity = (TextView) v.findViewById(R.id.rowPHumidity) ;
        rowRHumidity = (TextView) v.findViewById(R.id.rowRHumidity) ;
        rowPTemperature  = (TextView) v.findViewById(R.id.rowPTemperature) ;
        rowRTemperature = (TextView) v.findViewById(R.id.rowRTemperature) ;
        rowPMoisture = (TextView) v.findViewById(R.id.rowPMoisture) ;
        rowRMoisture = (TextView) v.findViewById(R.id.rowRMoisture) ;
        rowGrowthMonth = (TextView) v.findViewById(R.id.rowGrowthMonth) ;
        rowWinterMonth = (TextView) v.findViewById(R.id.rowWinterMonth) ;
        rowGrowthMoisture = (TextView) v.findViewById(R.id.rowGrowthMoisture) ;
        rowWinterMoisture = (TextView) v.findViewById(R.id.rowWinterMoisture) ;
        rowGrowingFrequency = (TextView) v.findViewById(R.id.rowGrowingFrequency) ;
        rowWinterFrequency = (TextView) v.findViewById(R.id.rowWinterFrequency) ;
        imageView = (ImageView) v.findViewById(R.id.imageViewPlantPhoto);
        buttonRefresh = (Button) v.findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleArduinoRequest(false, true);
            }
        });
        buttonWater = (Button) v.findViewById(R.id.buttonWater);
        buttonWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleArduinoRequest(true, false);
            }
        });
    }

    private void handleArduinoRequest(boolean pump, boolean fetch){
        Request request = new Request(user.getId(), user.getIp(), recipient.getByteAddress(),
                recipient.getMoisturePin(), recipient.getRelayPin(), pump, fetch);
        subscription.add(NetworkUtil.getRetrofit().requestArduinoAction(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Request request){
        ResponseHandlerThread thread = new ResponseHandlerThread(request.getId());
        thread.start();
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
            Toast.makeText(requireContext(), error.getMessage(),  Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    class ResponseHandlerThread extends Thread {
        String requestId;

        ResponseHandlerThread(String id) {
            this.requestId = id;
        }

        @Override
        public void run() {
            Log.d("Fetch", "startThread");
            int tries = 0;
            while(response == null){
               if(tries == 30){
                   freeRequest(requestId);
                   break;
               }
                subscription.add(NetworkUtil.getRetrofit().getResponse(requestId)
                        .observeOn(Schedulers.newThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tries++;
            }
        }

        private void handleResponse(Response r){
            response = r;
            Log.i("RESPONSE", r.toString());
            Handler threadHandler = new Handler(Looper.getMainLooper());
            threadHandler.post(new Runnable() {
                @Override
                public void run() {
                    rowRLight.setText(String.valueOf(r.getLight()));
                    rowRHumidity.setText(String.valueOf(r.getHumidity()));
                    rowRTemperature.setText(String.valueOf(r.getTemperature()));
                    rowRMoisture.setText(String.valueOf(r.getMoisture()));
                    Toast.makeText(requireContext(), r.getMessage(), Toast.LENGTH_SHORT);
                }
            });
            freeResponse(r.getId());
        }

        private void freeRequest(String requestId){
            subscription.add(NetworkUtil.getRetrofit().removeRequest(requestId)
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleDeleteRe, this::handleError));
        }

        private void freeResponse(String responseId) {
            subscription.add(NetworkUtil.getRetrofit().removeResponse(responseId)
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleDeleteRe, this::handleError));
        }

        private void handleDeleteRe(Void v){
            Log.i("RE", "All clear");
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
                Toast.makeText(requireContext(), error.getMessage(),  Toast.LENGTH_LONG).show();
            }
        }
    }
}
