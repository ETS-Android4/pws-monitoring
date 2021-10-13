package pws.monitoring.feri.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pws.monitoring.datalib.Recipient;
import pws.monitoring.feri.R;

public class WPlantsAdapter extends RecyclerView.Adapter<WPlantsAdapter.WPlantItem> {
    private ArrayList<Recipient> recipients;
    private String pickedDate;
    private Context context;

    public WPlantsAdapter(Context context, ArrayList<Recipient> recipients, String pickedDate) {
        this.recipients = recipients;
        this.pickedDate = pickedDate;
        this.context = context;
    }

    @NonNull
    @Override
    public WPlantItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wplant,
                parent, false);
        WPlantsAdapter.WPlantItem viewHolder = new WPlantsAdapter.WPlantItem(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WPlantItem holder, int position) {
        holder.bindValues(recipients.get(position), pickedDate);
    }

    @Override
    public int getItemCount() {
        return recipients.size();
    }


    public class WPlantItem extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView subtitle;

        public WPlantItem(@NonNull View itemView) {
            super(itemView);
            bindGUI(itemView);
        }

        private void bindGUI(View v) {
            title = (TextView) v.findViewById(R.id.textViewDateTimeWPlant);
            subtitle = (TextView) v.findViewById(R.id.textViewPlantNPin);

        }

        public void bindValues(Recipient r, String dateTime) {
            title.setText(dateTime);
            String sb = String.format(context.getResources().getString(R.string.multipart_text_wplant),
                    r.getPlant().getCommonName(),
                    r.getByteAddress(), String.valueOf(r.getRelayPin()));
            subtitle.setText(sb);

        }
    }
}
