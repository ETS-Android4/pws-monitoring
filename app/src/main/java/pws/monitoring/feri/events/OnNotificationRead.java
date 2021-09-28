package pws.monitoring.feri.events;

import pws.monitoring.datalib.Notification;

public class OnNotificationRead {
    Notification notification;

    public OnNotificationRead(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
