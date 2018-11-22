package tech.com.women_protection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tech.com.women_protection.Activities.MapsActivity;
import tech.com.women_protection.R;
import tech.com.women_protection.classes.Complaint;
import tech.com.women_protection.classes.LocationClass;

public class ListLocationNotificationAdapter extends ArrayAdapter<LocationClass> {

    Context context;
    List<LocationClass> list_location = new ArrayList<LocationClass>();
    List<Complaint> list_complaint = new ArrayList<Complaint>();

    public ListLocationNotificationAdapter(@NonNull Context context, List<Complaint> list_complaint, List<LocationClass> list_location) {
        super(context, R.layout.listview_emergency_requests, list_location);
        this.context = context;
        this.list_complaint = list_complaint;
        this.list_location = list_location;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_emergency_requests, null);
            for (int i = 0; i < list_complaint.size(); i++) {
                if (list_complaint.get(i).getComplaint_no().equalsIgnoreCase(list_location.get(position).getComplaint_no())) {
                    TextView textView_register_name = (TextView) convertView.findViewById(R.id.textView_emergency_requests);
                    textView_register_name.setText(list_complaint.get(position).getRegistered_by_name());
                }
            }


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("latitude", Double.valueOf(list_location.get(position).getLatitude()));
                    intent.putExtra("longitude", Double.valueOf(list_location.get(position).getLongitude()));
                    intent.putExtra("location", (Serializable) list_location.get(position));
                    context.startActivity(intent);
                }
            });


        } else {
            convertView = layoutInflater.inflate(R.layout.listview_emergency_requests, null);
            TextView textView_register_name = (TextView) convertView.findViewById(R.id.textView_emergency_requests);
            textView_register_name.setText(list_location.get(position).getComplaint_no());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("latitude", Double.valueOf(list_location.get(position).getLatitude()));
                    intent.putExtra("longitude", Double.valueOf(list_location.get(position).getLongitude()));
                    intent.putExtra("location", (Serializable) list_location.get(position));
                    context.startActivity(intent);
                }
            });


        }


        return convertView;
    }
}
