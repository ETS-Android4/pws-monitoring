package pws.monitoring.feri.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.activities.NavigationActivity;
import pws.monitoring.feri.events.OnUserUpdated;
import pws.monitoring.feri.network.NetworkUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UserUpdateService extends Service {
    private CompositeSubscription subscription;

    @Override
    public void onCreate() {
        super.onCreate();
        subscription = new CompositeSubscription();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, NavigationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, ApplicationState.CHANNEL_ID)
                .setContentTitle("Update Service")
                .setSmallIcon(R.drawable.ic_baseline_notifications_white)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (ApplicationState.runUpdateService){
                    subscription.add(NetworkUtil.getRetrofit().getUser(ApplicationState.loadLoggedUser().getId())
                            .observeOn(Schedulers.newThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(this::handleResponseUser, this::handleError));
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }

            private void handleResponseUser(User user) {
                EventBus.getDefault().post(new OnUserUpdated(user));
                Log.i("TESTING", user.toString());
            }

            private void handleError(Throwable error) {
                if (error instanceof HttpException) {
                    Gson gson = ApplicationState.getGson();
                    try {

                        String errorBody = ((HttpException) error).response().errorBody().string();
                        Log.i("ERROR!", errorBody);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getBaseContext(), error.getLocalizedMessage(),  Toast.LENGTH_LONG).show();
                }
            }

        }).start();

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
