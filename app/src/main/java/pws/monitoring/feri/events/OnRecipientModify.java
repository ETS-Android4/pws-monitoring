package pws.monitoring.feri.events;

import pws.monitoring.datalib.Recipient;

public class OnRecipientModify {
    Recipient recipient;

    public OnRecipientModify(Recipient recipient) {
        this.recipient = recipient;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }
}
