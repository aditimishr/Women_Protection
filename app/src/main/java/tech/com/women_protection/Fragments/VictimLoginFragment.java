package tech.com.women_protection.Fragments;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tech.com.women_protection.Activities.MainActivity;
import tech.com.women_protection.Activities.MapsActivity;
import tech.com.women_protection.Activities.ReportComplaintActivity;
import tech.com.women_protection.LocationListener.GpsTracker;
import tech.com.women_protection.R;
import tech.com.women_protection.classes.Complaint;
import tech.com.women_protection.classes.LocationClass;

public class VictimLoginFragment extends Fragment {
    Button button_clickhere_user, button_safe_location_user, button_mark_unsafe_location_user, button_generate_new_request_user, button_track_status_user;
    double lattitude, longitude;
    DatabaseReference database_complaints, database_location;
    DataSnapshot forLocation_snapshot;
    LocationClass locationClass;
    double safe_latitude, safe_longitude;
    String User_Type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_victim_login, container, false);
        button_clickhere_user = (Button) view.findViewById(R.id.button_clickhere_user);
        button_safe_location_user = (Button) view.findViewById(R.id.button_safe_location_user);
        button_mark_unsafe_location_user = (Button) view.findViewById(R.id.button_mark_unsafe_location_user);
        button_generate_new_request_user = (Button) view.findViewById(R.id.button_generate_new_request_user);
        button_track_status_user = (Button) view.findViewById(R.id.button_track_status_user);
        Bundle bundle = this.getArguments();
        User_Type = bundle.getString("user_type");
        String User_Name = bundle.getString("user_name");
        database_complaints = FirebaseDatabase.getInstance().getReference("Complaints");
        database_location = FirebaseDatabase.getInstance().getReference("Location");
        button_clickhere_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker g = new GpsTracker(getActivity());
                Location l = g.getLocation();
                if (l != null) {
                    sendEmergencyAlert(l, User_Name, database_complaints, database_location);
                    Toast.makeText(getActivity(), "Your Details along with your Location are sent.....Someone will arrive shortly", Toast.LENGTH_LONG).show();
                } else {
                    alert_for_gps();
                }
            }
        });
        button_safe_location_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("latitude", Double.valueOf(safe_latitude));
                intent.putExtra("longitude", Double.valueOf(safe_longitude));
                startActivity(intent);
            }
        });
        button_mark_unsafe_location_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker g = new GpsTracker(getActivity());
                Location l = g.getLocation();
                if (l != null) {
                    sendUnsafeLocationAlert(l,database_complaints,database_location);
                    Toast.makeText(getActivity(), "This Location is marked unsafe", Toast.LENGTH_LONG).show();
                } else {
                    alert_for_gps();
                }
            }
        });

        button_generate_new_request_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportComplaintActivity.class);
                intent.putExtra("user_name", User_Name);
                startActivity(intent);
            }
        });
        button_track_status_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    public void sendEmergencyAlert(Location l, String User_Name, DatabaseReference database_complaints, DatabaseReference database_location) {
        lattitude = l.getLatitude();
        longitude = l.getLongitude();
        String id = database_complaints.push().getKey();
        Complaint complaint = new Complaint();
        complaint.setComplaint_no(id);
        complaint.setHandled_by_admin_name("");
        complaint.setEmergency(true);
        complaint.setRegistered_by_name(User_Name);
        complaint.setGrievance_type("Emergency");
        if (User_Type != null && User_Type.equalsIgnoreCase("User")) {
            complaint.setRegistered_by_user_type("User");
        } else {
            complaint.setRegistered_by_user_type("Witness");
        }
        complaint.setStatus("New");
        database_complaints.child(id).setValue(complaint);
        LocationClass locationClass = new LocationClass();
        locationClass.setComplaint_no(id);
        locationClass.setLatitude(lattitude);
        locationClass.setLongitude(longitude);
        locationClass.setSafeLocation(false);
        locationClass.setStaus_location("New");
        database_location.child(id).setValue(locationClass);
    }

    public void alert_for_gps() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Gps Settings")
                .setCancelable(false)
                .setMessage("GPS is disabled Do you want to enable it?")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

    }

    @Override
    public void onStart() {
        getSafeLocation();
        super.onStart();
    }

    @Override
    public void onResume() {
        getSafeLocation();
        super.onResume();
    }

    public void getSafeLocation() {
        database_location.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forLocation_snapshot = dataSnapshot;
                for (DataSnapshot dataSnapshot1 : forLocation_snapshot.getChildren()) {
                    locationClass = dataSnapshot1.getValue(LocationClass.class);
                    if (locationClass.getSafeLocation() == true) {
                        safe_latitude = locationClass.getLatitude();
                        safe_longitude = locationClass.getLongitude();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendUnsafeLocationAlert(Location l, DatabaseReference database_complaints, DatabaseReference database_location) {
        String id = database_location.push().getKey();
        LocationClass locationClass = new LocationClass();
        locationClass.setComplaint_no(id);
        locationClass.setLatitude(l.getLatitude());
        locationClass.setLongitude(l.getLongitude());
        locationClass.setSafeLocation(false);
        locationClass.setStaus_location("New");
        database_location.child(id).setValue(locationClass);
        Complaint complaint = new Complaint();
        complaint.setComplaint_no(id);
        complaint.setHandled_by_admin_name("");
        complaint.setEmergency(true);
        if (User_Type != null && User_Type.equalsIgnoreCase("User")) {
            complaint.setRegistered_by_user_type("User");
        } else {
            complaint.setRegistered_by_user_type("Witness");
        }
        complaint.setGrievance_type("Unsafe");
        complaint.setStatus("New");
        database_complaints.child(id).setValue(complaint);
    }

}
