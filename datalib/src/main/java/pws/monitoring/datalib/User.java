package pws.monitoring.datalib;

import com.google.gson.annotations.SerializedName;

import java.io.Console;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class User {
    @SerializedName(value = "_id", alternate = "id")
    String id;
    String email;
    String password;
    String ip;
    ArrayList<Recipient> recipients;
    ArrayList<Notification> notifications;

    public User() {
    }

    public User(String email, String password, String ip) {
        this.email = email;
        this.password = password;
        this.ip = ip;
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


    public ArrayList<Calendar> getWateringDates() throws ParseException {
        ArrayList<String> dates = recipients.get(0).getWaterLog();
        for(int i=0; i < recipients.size(); i++){
            if(recipients.get(i).getWaterLog().size() > 0) {
                dates.remove(recipients.get(i).getWaterLog());
                dates.addAll(recipients.get(i).getWaterLog());
            }
        }
        System.out.println("Dates");
        System.out.println(dates);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        ArrayList<Calendar> calendars = new ArrayList<>();
        for(String d: dates){
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(d));
            calendars.add(cal);
        }
        return calendars;
    }

    public ArrayList<Recipient> getLogDateRecipients(String date){
        ArrayList<Recipient> r = new ArrayList<>();
        for(Recipient rec: recipients){
            if(rec.hasDate(date))
                r.add(rec);
        }

        System.out.println("Recs");
        System.out.println(r.toString());
        return r;
    }

    public boolean isUnread(){
        for (Notification n: notifications){
            if (!n.isRead())
                return true;
        }
        return false;
    }

}