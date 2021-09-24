package pws.monitoring.datalib;

import java.util.ArrayList;

public class User {
    String email;
    String password;
    String ip;
    ArrayList<ActivityLog> logger;
    ArrayList<Notification> notifications;

    public User() {
        logger = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    public User(String email, String password, String ip) {
        this.email = email;
        this.password = password;
        this.ip = ip;
        logger = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    public User(String email, String password, String ip,  ArrayList<ActivityLog> logger, ArrayList<Notification> notifications) {
        this.email = email;
        this.password = password;
        this.ip = ip;
        this.logger = logger;
        this.notifications = notifications;
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

    public ArrayList<ActivityLog> getLogger() {
        return logger;
    }

    public void setLogger(ArrayList<ActivityLog> logger) {
        this.logger = logger;
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
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", ip='" + ip + '\'' +
                ", logger=" + logger +
                ", notifications=" + notifications +
                '}';
    }

    public void addPlant(ActivityLog log){
        for(ActivityLog a: logger){
            if(!a.getPlant().getLatinName().equals(log.getPlant().getLatinName())){
                logger.add(log);
            }
        }
    }

    public void removePlant(ActivityLog log){
        for(ActivityLog a: logger){
            if(a.getPlant().getLatinName().equals(log.getPlant().getLatinName())){
                logger.remove(log);
            }
        }
    }

    public void addLog(Plant p, String dt){
        for(ActivityLog a: logger){
            if(a.getPlant().getLatinName().equals(p.getLatinName())){
                a.addDateTime(dt);
            }
        }
    }

    public void removeLog(Plant p, String dt){
        for(ActivityLog a: logger){
            if(a.getPlant().getLatinName().equals(p.getLatinName())){
                a.removeDateTime(dt);
            }
        }
    }

    public void addNotification(Notification notification){
        for(Notification n : notifications){
            if(!n.isIdentical(notification)){
                notifications.add(notification);
            }
        }
    }

    public void removeNotification(Notification notification){
        for(Notification n : notifications){
            if(n.isIdentical(notification)){
                notifications.remove(n);
            }
        }
    }

}