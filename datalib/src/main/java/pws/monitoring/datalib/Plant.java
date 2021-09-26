package pws.monitoring.datalib;

import com.google.gson.annotations.SerializedName;

public class Plant {
    @SerializedName(value = "_id", alternate = "id")
    String id;
    @SerializedName(value = "common_name", alternate = "commonName")
    String commonName;
    @SerializedName(value = "latin_name", alternate = "latinName")
    String latinName;
    int light;
    int humidity;
    int temperature;
    int moisture;
    int frequency;
    int modifier;

    public Plant() {
    }

    public Plant(String id, String commonName, String latinName, int light, int humidity, int temperature, int moisture, int frequency, int modifier) {
        this.id = id;
        this.commonName = commonName;
        this.latinName = latinName;
        this.light = light;
        this.humidity = humidity;
        this.temperature = temperature;
        this.moisture = moisture;
        this.frequency = frequency;
        this.modifier = modifier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getLatinName() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
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

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "id='" + id + '\'' +
                ", commonName='" + commonName + '\'' +
                ", latinName='" + latinName + '\'' +
                ", light=" + light +
                ", humidity=" + humidity +
                ", temperature=" + temperature +
                ", moisture=" + moisture +
                ", frequency=" + frequency +
                ", modifier=" + modifier +
                '}';
    }
}
