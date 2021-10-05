package pws.monitoring.datalib;

public class Response {
    String id;
    String userId;
    String recipientId;
    int light;
    int humidity;
    int temperature;
    int moisture;

    public Response(String id, String userId, String recipientId, int light, int humidity, int temperature, int moisture) {
        this.id = id;
        this.userId = userId;
        this.recipientId = recipientId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
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
                ", userId='" + userId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", light=" + light +
                ", humidity=" + humidity +
                ", temperature=" + temperature +
                ", moisture=" + moisture +
                '}';
    }
}
