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

import pws.monitoring.datalib.Recipient;
import pws.monitoring.feri.R;
import pws.monitoring.feri.events.OnRecipientModify;
import pws.monitoring.feri.events.OnRecipientShow;

public class RecipientAdapter extends RecyclerView.Adapter <RecipientAdapter
        .RecipientItem>{

    private ArrayList<Recipient> recipients;

    public RecipientAdapter(Context context, ArrayList<Recipient> recipients) {
        this.recipients = recipients;
    }

    @NonNull
    @Override
    public RecipientItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipient,
                parent, false);
        RecipientItem viewHolder = new RecipientItem(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipientItem holder, int position) {
        holder.bindValues(recipients.get(position));
    }


    @Override
    public int getItemCount() {
        return recipients.size();
    }

    public class RecipientItem extends RecyclerView.ViewHolder{
        private TextView commonName;
        private TextView macAddress;
        private Button buttonShowDetails;
        private Button buttonModify;

        public RecipientItem(@NonNull View itemView) {
            super(itemView);
            bindGUI(itemView);
        }

        private void bindGUI(View v) {
            commonName = (TextView) v.findViewById(R.id.textViewCommonName);
            macAddress = (TextView) v.findViewById(R.id.textViewMacAddress);
            buttonShowDetails = (Button) v.findViewById(R.id.buttonShowDetails);
            buttonModify = (Button) v.findViewById(R.id.buttonModify);

        }

        public void bindValues(Recipient r){
            commonName.setText(r.getPlant().getCommonName());
            macAddress.setText(r.getMacAddress());
            buttonShowDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new OnRecipientShow(r));
                }
            });
            buttonModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new OnRecipientModify(r));
                }
            });

        }
    }
}
