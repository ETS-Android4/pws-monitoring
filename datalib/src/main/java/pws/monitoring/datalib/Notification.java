package pws.monitoring.datalib;

public class Notification {
    String title;
    String type;
    String note;
    String dateTime;
    boolean read;

    public Notification() {
    }

    public Notification(String title, String type, String note, String dateTime, boolean read) {
        this.title = title;
        this.type = type;
        this.note = note;
        this.dateTime = dateTime;
        this.read = read;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", note='" + note + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", read=" + read +
                '}';
    }

    boolean isIdentical(Notification n){
        if(title.equals(n.getTitle()) && type.equals(n.getType())
        && note.equals(n.getNote()) && dateTime.equals(n.getDateTime())){
            return true;
        }
        return false;
    }
}
