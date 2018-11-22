package tech.com.women_protection.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tech.com.women_protection.Activities.AdapterActivity;
import tech.com.women_protection.Activities.Maps_for_unsafe_location;
import tech.com.women_protection.R;
import tech.com.women_protection.classes.Complaint;
import tech.com.women_protection.classes.LocationClass;

import static android.content.Context.MODE_PRIVATE;

public class AdminLoginFragment extends Fragment {
    Button button_emergency_request, button_notification, show_unsafe_location;
    DatabaseReference database_location, database_complaints;
    DataSnapshot forLocation_snapshot, forComplaint_snapshot;
    LocationClass locationClass;
    Complaint complaint;
    List<Complaint> list_complaint = new ArrayList<>();
    List<LocationClass> list_location = new ArrayList<>();
    List<LocationClass> list_location_not = new ArrayList<>();
    LinearLayout ll_listview, ll_main;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_login, container, false);
        button_emergency_request = (Button) view.findViewById(R.id.button_emergency_request);
        button_notification = (Button) view.findViewById(R.id.button_notification);
        show_unsafe_location = (Button) view.findViewById(R.id.show_unsafe_location);
        database_complaints = FirebaseDatabase.getInstance().getReference("Complaints");
        database_location = FirebaseDatabase.getInstance().getReference("Location");
        ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        Bundle bundle = this.getArguments();
        String User_Type = bundle.getString("User_Type");
        String User_Name = bundle.getString("User_Name");
        button_emergency_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetails();
                if (list_complaint.size() > 0) {
                    SharedPreferences preference = getActivity().getSharedPreferences("Requests", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("Requests", "Emergency");
                    editor.commit();
                    Intent intent = new Intent(getActivity(), AdapterActivity.class);
                    intent.putExtra("user_name", User_Name);
                    intent.putExtra("user_type", User_Type);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "No Emergency Requests", Toast.LENGTH_LONG).show();
                }
            }
        });
        button_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetails_notification();
                if (list_complaint.size() > 0) {
                    SharedPreferences preference = getActivity().getSharedPreferences("Requests", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("Requests", "Notification");
                    editor.commit();
                    Intent intent = new Intent(getActivity(), AdapterActivity.class);
                    intent.putExtra("user_name", User_Name);
                    intent.putExtra("user_type", User_Type);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "No Recent Notifications", Toast.LENGTH_LONG).show();
                }
            }
        });
        show_unsafe_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUnsafeLocationDetails();
                if (list_location.size() > 0) {
                    SharedPreferences preference = getActivity().getSharedPreferences("Requests", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("Requests", "Unsafe_Location");
                    editor.commit();
                    Intent intent = new Intent(getActivity(), Maps_for_unsafe_location.class);
                    intent.putExtra("user_name", User_Name);
                    intent.putExtra("user_type", User_Type);
                    intent.putExtra("list_unsafe", (Serializable) list_location);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        getEmergencyRequest();
        super.onStart();
    }

    @Override
    public void onResume() {
        getEmergencyRequest();
        super.onResume();
    }

    public void getEmergencyRequest() {
        database_complaints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forComplaint_snapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        database_location.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forLocation_snapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getDetails_notification() {
        if (forComplaint_snapshot != null && forLocation_snapshot != null) {
            for (DataSnapshot complaint_snapshot : forComplaint_snapshot.getChildren()) {
                for (DataSnapshot location_snapshot : forLocation_snapshot.getChildren()) {
                    complaint = complaint_snapshot.getValue(Complaint.class);
                    locationClass = location_snapshot.getValue(LocationClass.class);
                    if (complaint.getComplaint_no().equalsIgnoreCase(locationClass.getComplaint_no())) {
                        list_complaint.add(complaint);
                        list_location.add(locationClass);
                    }
                }
            }
        } else {
            getEmergencyRequest();
            Toast.makeText(getActivity(), "Some Error Ocurred....Press Again", Toast.LENGTH_LONG).show();
        }

    }

    public void getDetails() {
        if (forComplaint_snapshot != null && forLocation_snapshot != null) {
            for (DataSnapshot complaint_snapshot : forComplaint_snapshot.getChildren()) {
                for (DataSnapshot location_snapshot : forLocation_snapshot.getChildren()) {
                    complaint = complaint_snapshot.getValue(Complaint.class);
                    locationClass = location_snapshot.getValue(LocationClass.class);
                    if (complaint.getComplaint_no().equalsIgnoreCase(locationClass.getComplaint_no())) {
                        list_complaint.add(complaint);
                        list_location.add(locationClass);
                    }
                }
            }
        } else {
            getEmergencyRequest();
            Toast.makeText(getActivity(), "Some Error Ocurred....Press Again", Toast.LENGTH_LONG).show();
        }
    }

    public void getUnsafeLocationDetails() {
        if (forLocation_snapshot != null) {
            for (DataSnapshot location_snapshot : forLocation_snapshot.getChildren()) {
                locationClass = location_snapshot.getValue(LocationClass.class);
                if (locationClass.getSafeLocation() == false) {
                    list_location.add(locationClass);
                }
            }
        } else {
            getEmergencyRequest();
            Toast.makeText(getActivity(), "Some Error Ocurred....Press Again", Toast.LENGTH_LONG).show();
        }

    }


}
