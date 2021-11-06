package pws.monitoring.feri.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.activities.LogInActivity;
import pws.monitoring.feri.adapters.RecipientAdapter;
import pws.monitoring.feri.adapters.WPlantsAdapter;
import pws.monitoring.feri.config.ApplicationConfig;
import pws.monitoring.feri.events.OnFragmentChanged;
import pws.monitoring.feri.events.OnUserUpdated;

public class CalendarFragment extends Fragment {
    public static final String TAG =  CalendarFragment.class.getSimpleName();

    private RecyclerView recipientsRecyclerView;
    private WPlantsAdapter wPlantsAdapter;
    private CalendarView calendarView;

    private User user;
    private String strDate;
    private DateFormat dateFormat;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_calendar, container, false);

        user = ApplicationState.loadLoggedUser();
        dateFormat = new SimpleDateFormat(ApplicationConfig.DATE_FORMAT);

        bindGUI(rootView);
        bindValues();

        return rootView;
    }

    private void bindGUI(ViewGroup v) {
        recipientsRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewWPlants);
        calendarView = (CalendarView)v.findViewById(R.id.calendarView);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Date date = eventDay.getCalendar().getTime();
                strDate = dateFormat.format(date);
                Log.v(TAG, strDate);

                user = ApplicationState.loadLoggedUser();
                wPlantsAdapter = new WPlantsAdapter(requireContext(), user.getLogDateRecipients(strDate),
                        strDate);
                recipientsRecyclerView.setAdapter(wPlantsAdapter);
            }
        });

    }

    private void bindValues() {
        Date date = Calendar.getInstance().getTime();
        strDate = dateFormat.format(date);

        wPlantsAdapter = new WPlantsAdapter(requireContext(), user.getLogDateRecipients(strDate),
                strDate);
        recipientsRecyclerView.setAdapter(wPlantsAdapter);
        recipientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        calendarView.setDate(date);
        try {
            calendarView.setHighlightedDays((user.getWateringDates()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnUserUpdated event) {
        user = ApplicationState.loadLoggedUser();
        wPlantsAdapter = new WPlantsAdapter(requireContext(), user.getLogDateRecipients(strDate),
                strDate);
        recipientsRecyclerView.setAdapter(wPlantsAdapter);
    }

}
