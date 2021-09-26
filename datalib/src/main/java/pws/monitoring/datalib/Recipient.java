package pws.monitoring.datalib;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Recipient {
    @SerializedName(value = "_id", alternate = "id")
    String id;
    Plant plant;
    @SerializedName(value = "mac_address", alternate = "macAddress")
    String macAddress;
    int pin;
    @SerializedName(value = "watering_log", alternate = "wateringLog")
    ArrayList<String> wateringLog;

    public Recipient() {
    }

    public Recipient(String id, Plant plant, ArrayList<String> wateringLog, int pin, String macAddress) {
        this.id = id;
        this.plant = plant;
        this.wateringLog = wateringLog;
        this.pin = pin;
        this.macAddress = macAddress;
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

    public ArrayList<String> getWateringLog() {
        return wateringLog;
    }

    public void setWateringLog(ArrayList<String> wateringLog) {
        this.wateringLog = wateringLog;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
                "id='" + id + '\'' +
                ", plant=" + plant +
                ", wateringLog=" + wateringLog +
                ", pin=" + pin +
                ", macAddress='" + macAddress + '\'' +
                '}';
    }

    public void addDateTime(String dateTime){
        if(!wateringLog.contains(dateTime)) {
            wateringLog.add(dateTime);
        }
    }

    public void removeDateTime(String dateTime){
        if(wateringLog.contains(dateTime)) {
            wateringLog.remove(dateTime);
        }
    }
}
