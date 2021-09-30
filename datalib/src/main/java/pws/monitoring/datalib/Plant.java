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
    @SerializedName(value = "moisture_modifier", alternate = "moistureModifier")
    int moistureModifier;
    @SerializedName(value = "frequency_modifier", alternate = "frequencyModifier")
    int frequencyModifier;
    @SerializedName(value = "growth_month", alternate = "growingSeason")
    int growthMonth;
    @SerializedName(value = "hibernation_month", alternate = "hibernationSeason")
    int hibernationMonth;
    boolean customized;

    public Plant() {
    }

    public Plant(String id, String commonName, String latinName, int light, int humidity,
                 int temperature, int moisture, int frequency,
                 int moistureModifier, int frequencyModifier, int growthMonth,
                 int hibernationMonth, boolean customized) {
        this.id = id;
        this.commonName = commonName;
        this.latinName = latinName;
        this.light = light;
        this.humidity = humidity;
        this.temperature = temperature;
        this.moisture = moisture;
        this.frequency = frequency;
        this.moistureModifier = moistureModifier;
        this.frequencyModifier = frequencyModifier;
        this.growthMonth = growthMonth;
        this.hibernationMonth = hibernationMonth;
        this.customized = customized;
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

    public int getMoistureModifier() {
        return moistureModifier;
    }

    public int getFrequencyModifier() {
        return frequencyModifier;
    }

    public void setFrequencyModifier(int frequencyModifier) {
        this.frequencyModifier = frequencyModifier;
    }

    public int getGrowthMonth() {
        return growthMonth;
    }

    public void setGrowthMonth(int growthMonth) {
        this.growthMonth = growthMonth;
    }

    public int getHibernationMonth() {
        return hibernationMonth;
    }

    public void setHibernationMonth(int hibernationMonth) {
        this.hibernationMonth = hibernationMonth;
    }

    public void setMoistureModifier(int moistureModifier) {
        this.moistureModifier = moistureModifier;
    }

    public boolean isCustomized() {
        return customized;
    }

    public void setCustomized(boolean customized) {
        this.customized = customized;
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
                ", moistureModifier=" + moistureModifier +
                ", frequencyModifier=" + frequencyModifier +
                ", growthMonth=" + growthMonth +
                ", hibernationMonth=" + hibernationMonth +
                ", customized=" + customized +
                '}';
    }
}
