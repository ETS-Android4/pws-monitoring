package pws.monitoring.feri.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Objects;

import pws.monitoring.datalib.Notification;
import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.activities.NavigationActivity;
import pws.monitoring.feri.adapters.NotificationAdapter;
import pws.monitoring.feri.adapters.RecipientAdapter;
import pws.monitoring.feri.events.OnNotificationDelete;
import pws.monitoring.feri.events.OnNotificationRead;
import pws.monitoring.feri.events.OnRecipientShow;
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
                //search thru
            }
        });
        buttonFilter = (Button) v.findViewById(R.id.buttonFilter);
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //spinner
            }
        });
    }

    private void bindValues() {
        notificationAdapter = new NotificationAdapter(requireContext(), user.getNotifications());
        notificationRecyclerView.setAdapter(notificationAdapter);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscription.unsubscribe();
    }
}