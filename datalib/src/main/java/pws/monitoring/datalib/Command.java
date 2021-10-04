package pws.monitoring.datalib;

public class Command {
    String userId;
    Boolean activatePump;
    Boolean fetchSensoryData;
    Recipient recipient;

    public Command() {
    }

    public Command(String userId, Boolean activatePump, Boolean getSensoryData, Recipient recipient) {
        this.userId = userId;
        this.activatePump = activatePump;
        this.fetchSensoryData = getSensoryData;
        this.recipient = recipient;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getActivatePump() {
        return activatePump;
    }

    public void setActivatePump(Boolean activatePump) {
        this.activatePump = activatePump;
    }

    public Boolean getFetchSensoryData() {
        return fetchSensoryData;
    }

    public void setFetchSensoryData(Boolean fetchSensoryData) {
        this.fetchSensoryData = fetchSensoryData;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return "Command{" +
                "userId='" + userId + '\'' +
                ", activatePump=" + activatePump +
                ", fetchSensoryData=" + fetchSensoryData +
                ", recipient=" + recipient +
                '}';
    }
}
