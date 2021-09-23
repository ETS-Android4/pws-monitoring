package pws.monitoring.feri.network;

import pws.monitoring.datalib.User;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface RetrofitInterface {
    @POST("/users/")
    Observable<User> register(@Body User user);

    @POST("/users/login")
    Observable<User> login(@Body User user);
}
