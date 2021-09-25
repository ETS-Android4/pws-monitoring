package pws.monitoring.datalib;

public class Notification {
    String id;
    String title;
    String type;
    String note;
    String dateTime;
    boolean read;

    public Notification() {
    }

    public Notification(String id, String title, String type, String note, String dateTime, boolean read) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.note = note;
        this.dateTime = dateTime;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", note='" + note + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", read=" + read +
                '}';
    }
}
