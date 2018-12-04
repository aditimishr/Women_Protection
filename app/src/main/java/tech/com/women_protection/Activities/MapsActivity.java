package tech.com.women_protection.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tech.com.women_protection.R;
import tech.com.women_protection.classes.Complaint;
import tech.com.women_protection.classes.LocationClass;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lattitude, longitude;
    Bundle bundle;
    Complaint complaint;
    LocationClass locationClass;
    DatabaseReference database_complaints, database_location;
    String user_name, user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        toolbar.setTitle("Safety");
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        database_complaints = FirebaseDatabase.getInstance().getReference("Complaints");
        database_location = FirebaseDatabase.getInstance().getReference("Location");
        bundle = getIntent().getExtras();
        Intent intent = getIntent();
        SharedPreferences preference_user_type = getSharedPreferences("Login", MODE_PRIVATE);
        user_type = preference_user_type.getString("user_type", "");//"No name defined" is the default value.
        user_name = preference_user_type.getString("user_name", "");
        SharedPreferences preference = getSharedPreferences("Fragment", MODE_PRIVATE);
        String shared_fragment = preference.getString("Fragment", "");
        if (bundle != null) {
            lattitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            if (shared_fragment != null && !shared_fragment.equalsIgnoreCase("") && shared_fragment.equalsIgnoreCase("Admin")) {
                complaint = (Complaint) intent.getExtras().getSerializable("complaint");
                locationClass = (LocationClass) intent.getExtras().getSerializable("location");
                if (complaint != null) {
                    updateStatusDetails(complaint);
                }
                if (locationClass != null) {
                    updateLocationStatusDetails(locationClass);
                }

            }
        }
        /*GpsTracker g=new GpsTracker(getApplicationContext());
        Location l=g.getLocation();
        if(l!=null){
            lattitude=l.getLatitude();
            longitude=l.getLongitude();
            Toast.makeText(getApplicationContext(),"lattitude = "+lattitude+" and longitude = "+longitude,Toast.LENGTH_LONG).show();
        }
        else{
            new AlertDialog.Builder(this)
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
        }*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = 15f;
        LatLng myLocation = new LatLng(lattitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("Marker in Victim's Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("user_name", user_name);
        intent.putExtra("user_type", user_type);
        startActivity(intent);
        super.onBackPressed();
    }

    public void updateStatusDetails(Complaint complaint) {
        complaint.setStatus("Seen");
        database_complaints.child(complaint.getComplaint_no()).setValue(complaint);
    }

    public void updateLocationStatusDetails(LocationClass locationClass) {
        locationClass.setStaus_location("Seen");
        database_location.child(locationClass.getComplaint_no()).setValue(locationClass);
    }
}


