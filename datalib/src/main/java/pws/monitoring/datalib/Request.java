package pws.monitoring.datalib;

public class Request {
    String id;
    String userId;
    String recipientId;
    String deviceIp;
    String byteAddress;
    int moisturePin;
    int relayPin;
    Boolean activatePump;
    Boolean fetchSensoryData;

    public Request() {
    }

    public Request(String id, String userId, String recipientId, String deviceIp,
                   String byteAddress, int moisturePin, int relayPin, Boolean activatePump, Boolean fetchSensoryData) {
        this.id = id;
        this.userId = userId;
        this.recipientId = recipientId;
        this.deviceIp = deviceIp;
        this.byteAddress = byteAddress;
        this.moisturePin = moisturePin;
        this.relayPin = relayPin;
        this.activatePump = activatePump;
        this.fetchSensoryData = fetchSensoryData;
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

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getByteAddress() {
        return byteAddress;
    }

    public void setByteAddress(String byteAddress) {
        this.byteAddress = byteAddress;
    }

    public int getMoisturePin() {
        return moisturePin;
    }

    public void setMoisturePin(int moisturePin) {
        this.moisturePin = moisturePin;
    }

    public int getRelayPin() {
        return relayPin;
    }

    public void setRelayPin(int relayPin) {
        this.relayPin = relayPin;
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

    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", deviceIp='" + deviceIp + '\'' +
                ", byteAddress='" + byteAddress + '\'' +
                ", moisturePin=" + moisturePin +
                ", relayPin=" + relayPin +
                ", activatePump=" + activatePump +
                ", fetchSensoryData=" + fetchSensoryData +
                '}';
    }
}
