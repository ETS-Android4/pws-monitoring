package pws.monitoring.feri.fragments;

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
import pws.monitoring.feri.activities.LogInActivity;
import pws.monitoring.feri.config.ApplicationConfig;
import pws.monitoring.feri.network.NetworkError;
import pws.monitoring.feri.network.NetworkUtil;
import pws.monitoring.feri.util.MonthUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RecipientDetailsFragment extends Fragment {
    public static final String TAG =  RecipientDetailsFragment.class.getSimpleName();

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
        recipient = ApplicationState.getGson().fromJson(bundle.getString(ApplicationConfig.RECIPIENT_KEY),
                Recipient.class);

        bindGUI(rootView);
        bindValues();

        return rootView;
    }

    private void bindValues() {
        String plantText = String.format(getResources().getString(R.string.multipart_text_plant), recipient.getPlant().getCommonName(),
                recipient.getPlant().getLatinName());
        String recipientText = String.format(getResources().getString(R.string.multipart_text_recipient), recipient.getByteAddress(),
                String.valueOf(recipient.getRelayPin()), recipient.getMoisturePin());

        textViewPlantNames.setText(plantText);
        textViewPlantTechData.setText(recipientText);
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

        rowRLight.setText(getResources().getString(R.string.text_not_fetched));
        rowRHumidity.setText(getResources().getString(R.string.text_not_fetched));
        rowRTemperature.setText(getResources().getString(R.string.text_not_fetched));
        rowRMoisture.setText(getResources().getString(R.string.text_not_fetched));


        if(recipient.getPath()!= null){
            File imageFile = new File(recipient.getPath());
            if (imageFile.exists() && imageFile.canRead())
                Picasso.get().load(imageFile).resize(200, 200).centerCrop().into(imageView);
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
        Request request = new Request(user.getId(), recipient.getByteAddress(),
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
        Log.e(TAG, error.getMessage());
        NetworkError networkError = new NetworkError(error, requireContext());
        networkError.handleError();
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
            Log.d(TAG, "startThread");
            int tries = 0;
            while(response == null){
               if(tries == ApplicationConfig.TRIES_LIMIT){
                   freeRequest(requestId);
                   break;
               }
                subscription.add(NetworkUtil.getRetrofit().getResponse(requestId)
                        .observeOn(Schedulers.newThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                try {
                    Thread.sleep(ApplicationConfig.INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tries++;
            }
        }

        private void handleResponse(Response r){
            response = r;
            Log.i(TAG, r.toString());
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
            Log.i(TAG, "All clear");
        }

        private void handleError(Throwable error) {
            if (error instanceof HttpException) {
                try {
                    Log.d(TAG, ((HttpException) error).response().errorBody().string());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, error.getMessage());
            }
        }
    }
}
