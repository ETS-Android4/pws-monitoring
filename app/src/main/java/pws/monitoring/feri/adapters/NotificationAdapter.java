package pws.monitoring.feri.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import pws.monitoring.datalib.Notification;
import pws.monitoring.feri.R;
import pws.monitoring.feri.config.ApplicationConfig;
import pws.monitoring.feri.events.OnNotificationDelete;
import pws.monitoring.feri.events.OnNotificationRead;
import pws.monitoring.feri.events.OnRecipientModify;
import pws.monitoring.feri.events.OnRecipientShow;

public class NotificationAdapter extends RecyclerView.Adapter <NotificationAdapter.NotificationItem>{
    public ArrayList<Notification> notifications;
    private Context context;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,
                parent, false);
        return new NotificationItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationItem holder, int position) {
        holder.bindValues(notifications.get(position));
        if(!notifications.get(position).isRead())
            holder.itemView.setBackgroundColor(Color.parseColor("#ededed"));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationItem extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView note;
        private TextView dateTime;
        private ImageView imageView;
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
            imageView = (ImageView) v.findViewById(R.id.imageViewType);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage(context.getResources().getString(R.string.dialog_delete_notification))
                            .setPositiveButton(context.getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    EventBus.getDefault().post(new OnNotificationDelete(n));
                                }
                            })
                            .setNegativeButton(context.getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    builder.create().show();
                }
            });
            switch (n.getType()){
                case ApplicationConfig.WARNING_KEY:
                    imageView.setImageResource(R.drawable.ic_baseline_warning_24);
                    break;
                case ApplicationConfig.INFO_KEY:
                    imageView.setImageResource(R.drawable.ic_baseline_info_24);
                    break;
            }
        }
    }
}
