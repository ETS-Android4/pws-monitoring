package pws.monitoring.feri.network;

import java.util.ArrayList;

import pws.monitoring.datalib.Notification;
import pws.monitoring.datalib.Plant;
import pws.monitoring.datalib.Recipient;
import pws.monitoring.datalib.Request;
import pws.monitoring.datalib.Response;
import pws.monitoring.datalib.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface RetrofitInterface {

    @GET("/users/user/{id}")
    Observable<User> getUser(@Path("id") String id);

    @GET("/responses/{id}")
    Observable<Response> getResponse(@Path("id") String id);

    @POST("/users/register")
    Observable<User> register(@Body User user);

    @POST("/users/login")
    Observable<User> login(@Body User user);

    @POST("/users/logout")
    Observable<Void> logout();

    @POST("/recipients/{id}")
    Observable<User> createRecipient(@Path("id") String id, @Body Recipient recipient);

    @POST("/requests/")
    Observable<Request> requestArduinoAction(@Body Request request);

    @PUT("/users/update_user/{id}")
    Observable<User> updateUser(@Path("id") String id, @Body User user);

    @PUT("/recipients/{id}/{recipient_id}")
    Observable<User> updateRecipient(@Path("id") String id, @Path("recipient_id") String recipientId,
                                     @Body Recipient recipient);

    @PUT("/users/update_notification/{id}/{notification_id}")
    Observable<User> updateNotification(@Path("id") String id, @Path("notification_id") String notificationId,
                                        @Body Notification notification);

    @PUT("/plants/{id}/{recipient_id}/{plant_id}")
    Observable<User> updatePlant(@Path("id") String id, @Path("recipient_id") String recipientId,
                                       @Path("plant_id") String plantId, @Body Plant plant);

    @DELETE("/users/remove_user/{id}")
    Observable<Void> removeUser(@Path("id") String id);

    @DELETE("/recipients/{id}/{recipient_id}")
    Observable<User> removeRecipient(@Path("id") String id, @Path("recipient_id") String recipientId);

    @DELETE("/users/remove_notification/{id}/{notification_id}")
    Observable<User> removeNotification(@Path("id") String id,@Path("notification_id") String notificationId);

    @DELETE("/responses/{id}")
    Observable<Void> removeResponse(@Path("id") String id);

}
