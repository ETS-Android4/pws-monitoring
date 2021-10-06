package pws.monitoring.datalib;

import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName(value = "_id", alternate = "id")
    String id;
    @SerializedName(value = "request_id", alternate = "requestId")
    String requestId;
    String message;
    int light;
    int humidity;
    int temperature;
    int moisture;

    public Response(String id, String requestId, String message, int light, int humidity, int temperature, int moisture) {
        this.id = id;
        this.requestId = requestId;
        this.message = message;
        this.light = light;
        this.humidity = humidity;
        this.temperature = temperature;
        this.moisture = moisture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getMoisture() {
        return moisture;
    }

    public void setMoisture(int moisture) {
        this.moisture = moisture;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id='" + id + '\'' +
                ", requestId='" + requestId + '\'' +
                ", message='" + message + '\'' +
                ", light=" + light +
                ", humidity=" + humidity +
                ", temperature=" + temperature +
                ", moisture=" + moisture +
                '}';
    }
}
