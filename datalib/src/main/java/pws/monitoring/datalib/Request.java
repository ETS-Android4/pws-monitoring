package pws.monitoring.datalib;

import com.google.gson.annotations.SerializedName;

public class Request {
    @SerializedName(value = "_id", alternate = "id")
    String id;
    @SerializedName(value = "user_id", alternate = "userId")
    String userId;
    @SerializedName(value = "recipient_id", alternate = "recipientId")
    String recipientId;
    @SerializedName(value = "byte_address", alternate = "byteAddress")
    String byteAddress;
    @SerializedName(value = "moisture_pin", alternate = "moisturePin")
    int moisturePin;
    @SerializedName(value = "relay_pin", alternate = "relayPin")
    int relayPin;
    @SerializedName(value = "activate_pump", alternate = "activatePump")
    Boolean activatePump;
    @SerializedName(value = "fetch_data", alternate = "fetchSensoryData")
    Boolean fetchSensoryData;

    public Request() {
    }

    public Request(String userId, String recipientId, String byteAddress, int moisturePin, int relayPin, Boolean activatePump, Boolean fetchSensoryData) {
        this.userId = userId;
        this.recipientId = recipientId;
        this.byteAddress = byteAddress;
        this.moisturePin = moisturePin;
        this.relayPin = relayPin;
        this.activatePump = activatePump;
        this.fetchSensoryData = fetchSensoryData;
    }

    public Request(String id, String userId, String recipientId, String byteAddress, int moisturePin, int relayPin, Boolean activatePump, Boolean fetchSensoryData) {
        this.id = id;
        this.userId = userId;
        this.recipientId = recipientId;
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
                ", byteAddress='" + byteAddress + '\'' +
                ", moisturePin=" + moisturePin +
                ", relayPin=" + relayPin +
                ", activatePump=" + activatePump +
                ", fetchSensoryData=" + fetchSensoryData +
                '}';
    }
}
