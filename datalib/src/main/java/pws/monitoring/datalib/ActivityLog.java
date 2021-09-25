package pws.monitoring.datalib;

import java.util.ArrayList;

public class ActivityLog {
    String id;
    Plant plant;
    ArrayList<String> history;
    int pin;
    String macAddress;

    public ActivityLog() {
    }

    public ActivityLog(String id, Plant plant, ArrayList<String> history, int pin, String macAddress) {
        this.id = id;
        this.plant = plant;
        this.history = history;
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

    public ArrayList<String> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<String> history) {
        this.history = history;
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
                ", history=" + history +
                ", pin=" + pin +
                ", macAddress='" + macAddress + '\'' +
                '}';
    }

    public void addDateTime(String dateTime){
        if(!history.contains(dateTime)) {
            history.add(dateTime);
        }
    }

    public void removeDateTime(String dateTime){
        if(history.contains(dateTime)) {
            history.remove(dateTime);
        }
    }
}
