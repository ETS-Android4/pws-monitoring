package pws.monitoring.datalib;

public class Command {
    String userId;
    int pin;
    boolean water;

    public Command() {
    }

    public Command(String userId, int pin, boolean water) {
        this.userId = userId;
        this.pin = pin;
        this.water = water;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public boolean isWater() {
        return water;
    }

    public void setWater(boolean water) {
        this.water = water;
    }

    @Override
    public String toString() {
        return "Command{" +
                "userId='" + userId + '\'' +
                ", pin=" + pin +
                ", water=" + water +
                '}';
    }
}
