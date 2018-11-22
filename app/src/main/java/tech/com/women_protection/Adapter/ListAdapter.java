package tech.com.women_protection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.com.women_protection.Activities.MapsActivity;
import tech.com.women_protection.R;
import tech.com.women_protection.classes.Complaint;
import tech.com.women_protection.classes.LocationClass;

import static android.content.Context.MODE_PRIVATE;

public class ListAdapter extends ArrayAdapter<Complaint> {
    Context context;
    List<Complaint> list_complaint = new ArrayList<Complaint>();
    List<LocationClass> list_location = new ArrayList<LocationClass>();

    public ListAdapter(@NonNull Context context, List<Complaint> list_complaint, List<LocationClass> list_location) {
        super(context, R.layout.listview_emergency_requests, list_complaint);
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
            TextView textView_register_name = (TextView) convertView.findViewById(R.id.textView_emergency_requests);
            textView_register_name.setText(list_complaint.get(position).getRegistered_by_name());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("complaint", list_complaint.get(position));
                    for (int i = 0; i < list_location.size(); i++) {
                        if (list_location.get(i).getComplaint_no().equalsIgnoreCase(list_complaint.get(position).getComplaint_no())) {
                            intent.putExtra("location", list_location.get(i));
                            intent.putExtra("latitude", Double.valueOf(list_location.get(i).getLatitude()));
                            intent.putExtra("longitude", Double.valueOf(list_location.get(i).getLongitude()));
                        }
                    }
                    context.startActivity(intent);
                }
            });


        } else {
            convertView = layoutInflater.inflate(R.layout.listview_emergency_requests, null);
            TextView textView_register_name = (TextView) convertView.findViewById(R.id.textView_emergency_requests);
            textView_register_name.setText(list_complaint.get(position).getRegistered_by_name());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("complaint", list_complaint.get(position));
                    for (int i = 0; i < list_location.size(); i++) {
                        if (list_location.get(i).getComplaint_no().equalsIgnoreCase(list_complaint.get(position).getComplaint_no())) {
                            intent.putExtra("location", list_location.get(i));
                            intent.putExtra("latitude", Double.valueOf(list_location.get(i).getLatitude()));
                            intent.putExtra("longitude", Double.valueOf(list_location.get(i).getLongitude()));
                        }
                    }
                    context.startActivity(intent);
                }
            });


        }


        return convertView;
    }
}
