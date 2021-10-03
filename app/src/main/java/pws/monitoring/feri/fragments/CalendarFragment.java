package pws.monitoring.feri.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.adapters.WPlantsAdapter;

public class CalendarFragment extends Fragment {
    private RecyclerView recipientsRecyclerView;
    private WPlantsAdapter wPlantsAdapter;
    private CalendarView calendarView;

    User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_calendar, container, false);

        user = ApplicationState.loadLoggedUser();

        bindGUI(rootView);
        bindValues();

        return rootView;
    }

    private void bindGUI(ViewGroup v) {
        recipientsRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewWPlants);
        calendarView = (CalendarView)v.findViewById(R.id.calendarView);

    }

    private void bindValues() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);

        wPlantsAdapter = new WPlantsAdapter(requireContext(), user.getRecipients(), strDate);
        recipientsRecyclerView.setAdapter(wPlantsAdapter);
        recipientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //TODO HIGHLIGHT
    }

}
