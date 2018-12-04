package tech.com.women_protection.Fragments;

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

import tech.com.women_protection.Activities.MapsActivity;
import tech.com.women_protection.LocationListener.GpsTracker;
import tech.com.women_protection.R;
import tech.com.women_protection.classes.Complaint;
import tech.com.women_protection.classes.LocationClass;

public class WitnessLoginFragment extends Fragment {
    Button button_clickhere_witness, button_safe_location, button_mark_unsafe_location;
    double lattitude, longitude;
    DatabaseReference database_complaints, database_location;
    DataSnapshot forLocation_snapshot;
    LocationClass locationClass;
    double safe_latitude, safe_longitude;
    VictimLoginFragment victimLoginFragment = new VictimLoginFragment();
    String User_Type, User_Name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_witness_login, container, false);
        button_clickhere_witness = (Button) view.findViewById(R.id.button_clickhere_witness);
        button_safe_location = (Button) view.findViewById(R.id.button_safe_location);
        button_mark_unsafe_location = (Button) view.findViewById(R.id.button_mark_unsafe_location);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            User_Type = bundle.getString("user_type");
            User_Name = bundle.getString("user_name");
        }
        database_complaints = FirebaseDatabase.getInstance().getReference("Complaints");
        database_location = FirebaseDatabase.getInstance().getReference("Location");
        button_clickhere_witness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker g = new GpsTracker(getActivity());
                Location l = g.getLocation();
                if (l != null) {
                    victimLoginFragment.sendEmergencyAlert(l, User_Name, database_complaints, database_location);
                    Toast.makeText(getActivity(), "Your Details along with your Location are sent.....Someone will arrive shortly", Toast.LENGTH_LONG).show();
                } else {
                    victimLoginFragment.alert_for_gps();
                }
            }
        });
        button_safe_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("latitude", Double.valueOf(safe_latitude));
                intent.putExtra("longitude", Double.valueOf(safe_longitude));
                startActivity(intent);
            }
        });
        button_mark_unsafe_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker g = new GpsTracker(getActivity());
                Location l = g.getLocation();
                if (l != null) {
                    victimLoginFragment.sendUnsafeLocationAlert(l, database_complaints, database_location);
                    Toast.makeText(getActivity(), "This Location is marked unsafe", Toast.LENGTH_LONG).show();
                } else {
                    victimLoginFragment.alert_for_gps();
                }
            }
        });
        return view;
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
}
