package pws.monitoring.feri.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import pws.monitoring.datalib.Notification;
import pws.monitoring.feri.R;
import pws.monitoring.feri.events.OnNotificationDelete;
import pws.monitoring.feri.events.OnNotificationRead;
import pws.monitoring.feri.events.OnRecipientModify;
import pws.monitoring.feri.events.OnRecipientShow;

public class NotificationAdapter extends RecyclerView.Adapter <NotificationAdapter.NotificationItem>{
    private ArrayList<Notification> notifications;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipient,
                parent, false);
        NotificationAdapter.NotificationItem viewHolder = new NotificationAdapter.NotificationItem(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationItem holder, int position) {
        holder.bindValues(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationItem extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView note;
        private TextView dateTime;
        private Button buttonRead;
        private Button buttonDelete;

        public NotificationItem(@NonNull View itemView) {
            super(itemView);
            bindGUI(itemView);

        }

        private void bindGUI(View v) {
            title = (TextView) v.findViewById(R.id.textViewTitle);
            note = (TextView) v.findViewById(R.id.textViewNote);
            dateTime = (TextView) v.findViewById(R.id.textViewDateTime);
            buttonRead = (Button) v.findViewById(R.id.buttonRead);
            buttonDelete = (Button) v.findViewById(R.id.buttonDelete);

        }

        public void bindValues(Notification n) {
            title.setText(n.getTitle());
            note.setText(n.getNote());
            dateTime.setText(n.getDateTime());
            buttonRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new OnNotificationRead(n));
                }
            });
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new OnNotificationDelete(n));
                }
            });
        }
    }
}
