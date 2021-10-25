package pws.monitoring.datalib;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Recipient {
    @SerializedName(value = "_id", alternate = "id")
    String id;
    Plant plant;
    String path;
    @SerializedName(value = "byte_address", alternate = "byteAddress")
    String byteAddress;
    @SerializedName(value = "relay_pin", alternate = "relayPin")
    int relayPin;
    @SerializedName(value = "moisture_pin", alternate = "moisturePin")
    String moisturePin;
    @SerializedName(value = "water_log", alternate = "waterLog")
    ArrayList<String> waterLog;

    public Recipient() {
        waterLog = new ArrayList<>();
    }

    public Recipient(String id, Plant plant, String path, String byteAddress, int relayPin, String moisturePin, ArrayList<String> waterLog) {
        this.id = id;
        this.plant = plant;
        this.path = path;
        this.byteAddress = byteAddress;
        this.relayPin = relayPin;
        this.moisturePin = moisturePin;
        this.waterLog = waterLog;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<String> getWaterLog() {
        return waterLog;
    }

    public void setWaterLog(ArrayList<String> waterLog) {
        this.waterLog = waterLog;
    }

    public int getRelayPin() {
        return relayPin;
    }

    public void setRelayPin(int relayPin) {
        this.relayPin = relayPin;
    }

    public String getByteAddress() {
        return byteAddress;
    }

    public void setByteAddress(String byteAddress) {
        this.byteAddress = byteAddress;
    }

    public String getMoisturePin() {
        return moisturePin;
    }

    public void setMoisturePin(String moisturePin) {
        this.moisturePin = moisturePin;
    }

    public static boolean validatePins(String byteAddress, String moisturePin, int relayPin){
        return Pattern.matches("[0-1]{4}", byteAddress)
                && Pattern.matches("A[0-9]", moisturePin) && (relayPin > 20 && relayPin < 54);
    }

    @Override
    public String toString() {
        return "Recipient{" +
                "id='" + id + '\'' +
                ", plant=" + plant +
                ", path ='" + path + '\'' +
                ", byteAddress='" + byteAddress + '\'' +
                ", relayPin=" + relayPin +
                ", moisturePin=" + moisturePin +
                ", waterLog=" + waterLog +
                '}';
    }

    public void addDateTime(String dateTime){
        if(!waterLog.contains(dateTime)) {
            waterLog.add(dateTime);
        }
    }

    public void removeDateTime(String dateTime){
        if(waterLog.contains(dateTime)) {
            waterLog.remove(dateTime);
        }
    }

    public boolean hasDate(String dateTime){
        for(String dt : waterLog){
            if(dt.contains(dateTime))
                return true;
        }
        return false;
    }
}
