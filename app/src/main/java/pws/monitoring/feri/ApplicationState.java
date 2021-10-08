package pws.monitoring.feri;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.config.ApplicationConfig;

public class ApplicationState extends Application {
    public static final String CHANNEL_ID = "userUpdateServiceChannel";
    public static final String CHANNEL_ID_NOTIFICATIONS = "notificationsChannel";

    private static Gson gson;
    private static User loggedUser;
    public static SharedPreferences sharedPreferences;

    public static boolean runUpdateService;


    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        runUpdateService = true;
    }

    public static Gson getGson() {
        if (gson == null)
            gson = new GsonBuilder().setPrettyPrinting().create();
        return gson;
    }

    public static boolean checkLoggedUser(){
        if (loadLoggedUser() != null) {
            return true;
        }
        return false;
    }

    public static User loadLoggedUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = sharedPreferences.getString(ApplicationConfig.USER_KEY, null);
        if (json != null) {
            loggedUser = getGson().fromJson(json, User.class);
            editor.apply();
            return loggedUser;
        }
        return null;
    }

    public static void saveLoggedUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = getGson().toJson(user);
        editor.putString(ApplicationConfig.USER_KEY, json);
        editor.apply();
    }
}
