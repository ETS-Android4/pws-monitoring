package pws.monitoring.feri.network;

import java.util.ArrayList;

import pws.monitoring.datalib.Notification;
import pws.monitoring.datalib.Plant;
import pws.monitoring.datalib.Recipient;
import pws.monitoring.datalib.User;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface RetrofitInterface {
    @GET("/plants/")
    Observable<ArrayList<Plant>> getPlants();

    @GET("/plant/{id}")
    Observable<Plant> getPlant(@Path("id") String id);

    @GET("/user/{id}")
    Observable<Plant> getUser(@Path("id") String id);

    @POST("/users/register")
    Observable<User> register(@Body User user);

    @POST("/users/login")
    Observable<User> login(@Body User user);

    @POST("/users/logout")
    Observable<Void> logout();

    @POST("/users/{id}/recipient")
    Observable<User> createRecipient(@Path("id") String id, @Body Recipient recipient);

    @PUT("/users/update_user/{id}")
    Observable<User> updateUser(@Path("id") String id, @Body User user);

    @PUT("/users/{id}/update_recipient/{recipient_id}")
    Observable<User> updateRecipient(@Path("id") String id, @Path("recipient_id") String recipientId,
                                     @Body Recipient recipient);

    @PUT("/users/{id}/update_notification/{notification_id}")
    Observable<User> updateNotification(@Path("id") String id, @Path("notification_id") String notificationId,
                                        @Body Notification notification);

    @DELETE("/users/remove_user/{id}")
    Observable<Void> removeUser(@Path("id") String id);

    @DELETE("/users/{id}/remove_recipient/{recipient_id}")
    Observable<User> removeRecipient(@Path("id") String id,@Path("recipient_id") String recipientId);

    @DELETE("/users/{id}/remove_notification/{notification_id}")
    Observable<User> removeNotification(@Path("id") String id,@Path("notification_id") String notificationId);

}
