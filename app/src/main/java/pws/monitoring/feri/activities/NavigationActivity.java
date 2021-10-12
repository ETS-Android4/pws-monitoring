package pws.monitoring.feri.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import pws.monitoring.datalib.Notification;
import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.databinding.ActivityNavigationBinding;
import pws.monitoring.feri.events.OnNotificationRead;
import pws.monitoring.feri.events.OnUserUpdated;
import pws.monitoring.feri.services.UserUpdateService;


public class NavigationActivity extends AppCompatActivity {
    private ActivityNavigationBinding binding;
    private BottomNavigationView navView;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_account, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigation);
        NavigationUI.setupWithNavController(binding.navView, navController);

        startService();

    }


    public void startService() {
        Intent serviceIntent = new Intent(this, UserUpdateService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
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
        User savedUser = ApplicationState.loadLoggedUser();
        if(savedUser.getNotifications().size() < event.getUser().getNotifications().size()){
            int indexPadding = event.getUser().getNotifications().size()
                    - savedUser.getNotifications().size();
            int lastIndex = event.getUser().getNotifications().size();
            ArrayList<Notification> notifications = new ArrayList<>(event.getUser()
                    .getNotifications().subList(lastIndex - indexPadding, lastIndex));

            for(Notification n: notifications){
                if(n.getType().equals("Warning")){
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                            ApplicationState.CHANNEL_ID_NOTIFICATIONS)
                            .setSmallIcon(R.drawable.ic_baseline_warning_24)
                            .setStyle(new NotificationCompat.BigTextStyle())
                            .setContentTitle(n.getTitle())
                            .setContentText(n.getNote())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(2, builder.build());

                } else {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                            ApplicationState.CHANNEL_ID_NOTIFICATIONS)
                            .setSmallIcon(R.drawable.ic_baseline_info_24)
                            .setStyle(new NotificationCompat.BigTextStyle())
                            .setContentTitle(n.getTitle())
                            .setContentText(n.getNote())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(3, builder.build());
                }
            }
        }

        ApplicationState.saveLoggedUser(event.getUser());

       if(ApplicationState.loadLoggedUser() != null && ApplicationState.loadLoggedUser().isUnread())
           navView.getMenu().getItem(2).setIcon(R.drawable.ic_baseline_notifications_active_24);
       else
           navView.getMenu().getItem(2).setIcon(R.drawable.ic_baseline_notifications_white);

    }

}