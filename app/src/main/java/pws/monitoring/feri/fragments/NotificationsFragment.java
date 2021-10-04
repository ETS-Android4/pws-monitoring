package pws.monitoring.feri.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import pws.monitoring.datalib.Notification;
import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.activities.NavigationActivity;
import pws.monitoring.feri.adapters.NotificationAdapter;
import pws.monitoring.feri.adapters.RecipientAdapter;
import pws.monitoring.feri.events.OnFilterSelected;
import pws.monitoring.feri.events.OnNotificationDelete;
import pws.monitoring.feri.events.OnNotificationRead;
import pws.monitoring.feri.events.OnRecipientShow;
import pws.monitoring.feri.events.OnUserUpdated;
import pws.monitoring.feri.network.NetworkUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class NotificationsFragment extends Fragment {
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;

    EditText edtSearch;
    Button buttonSearch;
    Button buttonFilter;

    private CompositeSubscription subscription;

    User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_notifications, container, false);

        subscription = new CompositeSubscription();
        user = ApplicationState.loadLoggedUser();

        bindGUI(rootView);
        bindValues();

        return rootView;
    }

    private void bindGUI(ViewGroup v) {
        notificationRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewNotifications);
        edtSearch = (EditText) v.findViewById(R.id.edtSearchNotifications);
        buttonSearch = (Button) v.findViewById(R.id.buttonSearchNotifications);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = ApplicationState.loadLoggedUser();
                if(!edtSearch.getText().toString().equals("")){
                    ArrayList<Notification> matched = new ArrayList<>();
                    for(Notification n: user.getNotifications()){
                        if(n.getTitle().contains(edtSearch.getText().toString()) ||
                                n.getType().contains(edtSearch.getText().toString()))
                            matched.add(n);
                    }
                    user.setNotifications(matched);
                }
                EventBus.getDefault().post(new OnUserUpdated());
            }
        });
        buttonFilter = (Button) v.findViewById(R.id.buttonFilter);
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
    }

    private void bindValues() {
        notificationAdapter = new NotificationAdapter(requireContext(), user.getNotifications());
        notificationRecyclerView.setAdapter(notificationAdapter);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ascending:
                        EventBus.getDefault().post(new OnFilterSelected(0));
                        return true;
                    case R.id.descending:
                        EventBus.getDefault().post(new OnFilterSelected(1));
                        return true;
                    case R.id.unread:
                        EventBus.getDefault().post(new OnFilterSelected(2));
                        return true;
                    case R.id.read:
                        EventBus.getDefault().post(new OnFilterSelected(3));
                        return true;
                    case R.id.all:
                        EventBus.getDefault().post(new OnFilterSelected(4));
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.filter_menu);
        popup.show();
    }


    private void updateNotification(Notification n){
        subscription.add(NetworkUtil.getRetrofit().updateNotification(user.getId(), n.getId(), n)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdate, this::handleError));
    }

    private void deleteNotification(Notification n){
        subscription.add(NetworkUtil.getRetrofit().removeNotification(user.getId(), n.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseDelete, this::handleError));
    }

    private void handleResponseUpdate(User user){
        ApplicationState.saveLoggedUser(user);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_navigation, new NotificationsFragment()).commit();
    }

    private void handleResponseDelete(User user){
        ApplicationState.saveLoggedUser(user);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_navigation, new NotificationsFragment()).commit();
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
    public void onMessageEvent(OnNotificationRead event) {
        updateNotification(event.getNotification());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnNotificationDelete event) {
        deleteNotification(event.getNotification());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnUserUpdated event) {
        notificationAdapter = new NotificationAdapter(requireContext(), user.getNotifications());
        notificationRecyclerView.setAdapter(notificationAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnFilterSelected event) {
        switch (event.getFilter()){
            case 0:
                Collections.sort(user.getNotifications(), new Comparator<Notification>(){
                    public int compare(Notification n1, Notification n2){
                        try {
                            Date d1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(n1.getDateTime());
                            Date d2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(n2.getDateTime());
                            Log.i("Sorting", String.valueOf(d1.compareTo(d2)));
                            return d1.compareTo(d2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                break;
            case 1:
                Collections.sort(user.getNotifications(), new Comparator<Notification>(){
                    public int compare(Notification n1, Notification n2){
                        try {
                            Date d1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(n1.getDateTime());
                            Date d2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(n2.getDateTime());
                            Log.i("Sorting", String.valueOf(d1.compareTo(d2)));
                            return d2.compareTo(d1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                break;
            case 2:
                user = ApplicationState.loadLoggedUser();
                ArrayList<Notification> unread = new ArrayList<>();
                for(Notification n: user.getNotifications()){
                    if(!n.isRead())
                        unread.add(n);
                }
                user.setNotifications(unread);
                break;
            case 3:
                user = ApplicationState.loadLoggedUser();
                ArrayList<Notification> read = new ArrayList<>();
                for(Notification n: user.getNotifications()){
                    if(n.isRead())
                        read.add(n);
                }
                user.setNotifications(read);
                break;
            case 4:
                user = ApplicationState.loadLoggedUser();
        }
        Log.i("Notifications sorted", user.getNotifications().toString());
        EventBus.getDefault().post(new OnUserUpdated());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscription.unsubscribe();
    }
}