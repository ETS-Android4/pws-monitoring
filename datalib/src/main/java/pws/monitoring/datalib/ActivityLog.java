package pws.monitoring.datalib;

import java.util.ArrayList;

public class ActivityLog {
    Plant plant;
    ArrayList<String> history;
    int pin;

    public ActivityLog(Plant plant, ArrayList<String> history, int pin) {
        this.plant = plant;
        this.history = new ArrayList<>();
        this.pin = pin;
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

    @Override
    public String toString() {
        return "ActivityLog{" +
                "plant=" + plant +
                ", history=" + history +
                ", pin=" + pin +
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
