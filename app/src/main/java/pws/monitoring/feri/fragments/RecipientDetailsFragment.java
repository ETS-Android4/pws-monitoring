package pws.monitoring.feri.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.Calendar;

import pws.monitoring.datalib.Recipient;
import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.util.MonthUtil;

public class RecipientDetailsFragment extends Fragment {
    User user;
    Recipient recipient;

    TextView textViewPlantNames;
    TextView textViewPlantTechData;
    TextView rowPLight;
    TextView rowRLight;
    TextView rowPHumidity;
    TextView rowRHumidity;
    TextView rowPTemperature;
    TextView rowRTemperature;
    TextView rowPMoisture;
    TextView rowRMoisture;
    TextView rowGrowthMonth;
    TextView rowWinterMonth;
    TextView rowGrowthMoisture;
    TextView rowWinterMoisture;
    TextView rowGrowingFrequency;
    TextView rowWinterFrequency;
    Button buttonRefresh;
    Button buttonWater;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_recipient_details, container, false);

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
                " and pin " + String.valueOf(recipient.getRelayPin()));
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

        //TODO bind sensor data to rows
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
        buttonRefresh = (Button) v.findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add command to refresh data from table
            }
        });
        buttonWater = (Button) v.findViewById(R.id.buttonWater);
        buttonWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add command to water the plant
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
