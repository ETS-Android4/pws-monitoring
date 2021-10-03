package pws.monitoring.feri.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pws.monitoring.feri.config.ApplicationConfig;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class NetworkUtil {
    public static RetrofitInterface getRetrofit(){

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        return new Retrofit.Builder()
                .baseUrl(ApplicationConfig.PWS_SERVER)
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitInterface.class);

    }
}
