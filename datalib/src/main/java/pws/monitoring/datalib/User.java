package pws.monitoring.datalib;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class User {
    @SerializedName(value = "_id", alternate = "id")
    String id;
    String email;
    String password;
    String ip;
    ArrayList<Recipient> recipients;
    ArrayList<Notification> notifications;

    public User() {
        recipients = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    public User(String email, String password, String ip) {
        this.email = email;
        this.password = password;
        this.ip = ip;
        recipients = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    public User(String id, String email, String password, String ip, ArrayList<Recipient> recipients, ArrayList<Notification> notifications) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.ip = ip;
        this.recipients = recipients;
        this.notifications = notifications;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ArrayList<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(ArrayList<Recipient> recipients) {
        this.recipients = recipients;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", ip='" + ip + '\'' +
                ", recipients=" + recipients +
                ", notifications=" + notifications +
                '}';
    }

    public void addPlant(Recipient log){
        for(Recipient a: recipients){
            if(!a.getPlant().getLatinName().equals(log.getPlant().getLatinName())){
                recipients.add(log);
            }
        }
    }

    public void removePlant(Recipient log){
        for(Recipient a: recipients){
            if(a.getPlant().getLatinName().equals(log.getPlant().getLatinName())){
                recipients.remove(log);
            }
        }
    }

    public void addLog(Plant p, String dt){
        for(Recipient a: recipients){
            if(a.getPlant().getLatinName().equals(p.getLatinName())){
                a.addDateTime(dt);
            }
        }
    }

    public void removeLog(Plant p, String dt){
        for(Recipient a: recipients){
            if(a.getPlant().getLatinName().equals(p.getLatinName())){
                a.removeDateTime(dt);
            }
        }
    }

    public void addNotification(Notification notification){
        for(Notification n : notifications){
            if(!n.getId().equals(notification.getId())){
                notifications.add(notification);
            }
        }
    }

    public void removeNotification(Notification notification){
        for(Notification n : notifications){
            if(n.getId().equals(notification.getId())){
                notifications.remove(n);
            }
        }
    }

}