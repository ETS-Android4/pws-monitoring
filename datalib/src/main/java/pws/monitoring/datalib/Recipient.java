package pws.monitoring.datalib;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Recipient {
    @SerializedName(value = "_id", alternate = "id")
    String id;
    Plant plant;
    @SerializedName(value = "byte_address", alternate = "byteAddress")
    String byteAddress;
    @SerializedName(value = "relay_pin", alternate = "relayPin")
    int relayPin;
    @SerializedName(value = "water_log", alternate = "waterLog")
    ArrayList<String> waterLog;

    public Recipient() {
        waterLog = new ArrayList<>();
    }

    public Recipient(String id, Plant plant, ArrayList<String> waterLog, int relayPin, String byteAddress) {
        this.id = id;
        this.plant = plant;
        this.waterLog = waterLog;
        this.relayPin = relayPin;
        this.byteAddress = byteAddress;
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

    @Override
    public String toString() {
        return "Recipient{" +
                "id='" + id + '\'' +
                ", plant=" + plant +
                ", byteAddress='" + byteAddress + '\'' +
                ", relayPin=" + relayPin +
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
            if(dt.equals(dateTime))
                return true;
        }
        return false;
    }
}
