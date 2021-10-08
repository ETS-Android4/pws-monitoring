package pws.monitoring.feri.events;

import pws.monitoring.datalib.User;

public class OnUserUpdated {
    User user;

    public OnUserUpdated(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
