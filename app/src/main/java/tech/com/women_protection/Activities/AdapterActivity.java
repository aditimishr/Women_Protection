package tech.com.women_protection.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tech.com.women_protection.Fragments.VictimLoginFragment;
import tech.com.women_protection.Fragments.WitnessLoginFragment;
import tech.com.women_protection.ListAdapter;
import tech.com.women_protection.ListLocationNotificationAdapter;
import tech.com.women_protection.LocationListener.GpsTracker;
import tech.com.women_protection.R;
import tech.com.women_protection.classes.Complaint;
import tech.com.women_protection.classes.LocationClass;

import android.support.design.widget.NavigationView;
import android.widget.Toast;

public class AdapterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ListView lv_emergency_requests, lv_complaint_notification, lv_location_notification;
    List<Complaint> lst_complaint = new ArrayList<>();
    List<LocationClass> lst_location = new ArrayList<>();
    List<LocationClass> lst_location_unsafe = new ArrayList<>();
    LinearLayout ll_emergency_request;
    DatabaseReference database_location, database_complaints;
    DataSnapshot forLocation_snapshot, forComplaint_snapshot;
    LocationClass locationClass;
    Complaint complaint;
    String request_type;
    Toolbar toolbar;
    ScrollView scrollview_notifications;
    TextView textview_emergency_request, textview_complaint_notification, textview_location;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    List<LocationClass> list_location = new ArrayList<LocationClass>();
    Bundle bundle;
    String user_name, user_type;
    VictimLoginFragment victimLoginFragment = new VictimLoginFragment();
    Double safe_latitude, safe_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_adapter);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_adapter);
        navigationView.getMenu().setGroupVisible(R.id.group_admin, true);
        navigationView.setNavigationItemSelectedListener(this);
        lv_emergency_requests = (ListView) findViewById(R.id.lv_emergency_requests);
        lv_complaint_notification = (ListView) findViewById(R.id.lv_complaint_notification);
        lv_location_notification = (ListView) findViewById(R.id.lv_location_notification);
        ll_emergency_request = (LinearLayout) findViewById(R.id.ll_emergency_request);
        scrollview_notifications = (ScrollView) findViewById(R.id.scrollview_notifications);
        textview_emergency_request = (TextView) findViewById(R.id.textview_emergency_request);
        textview_complaint_notification = (TextView) findViewById(R.id.textview_complaint_notification);
        textview_location = (TextView) findViewById(R.id.textview_location);
        database_complaints = FirebaseDatabase.getInstance().getReference("Complaints");
        database_location = FirebaseDatabase.getInstance().getReference("Location");
        SharedPreferences preference = getSharedPreferences("Requests", MODE_PRIVATE);
        request_type = preference.getString("Requests", "");
        bundle = getIntent().getExtras();
        if (bundle != null) {
            user_name = bundle.getString("user_name");
            user_type = bundle.getString("user_type");
        }

    }

    public void getDetails() {
        //getEmergencyRequest();
        lst_complaint.clear();
        lst_location.clear();
        for (DataSnapshot complaint_snapshot : forComplaint_snapshot.getChildren()) {
            for (DataSnapshot location_snapshot : forLocation_snapshot.getChildren()) {
                complaint = complaint_snapshot.getValue(Complaint.class);
                locationClass = location_snapshot.getValue(LocationClass.class);
                if (complaint.getComplaint_no().equalsIgnoreCase(locationClass.getComplaint_no()) && complaint.getEmergency() == true && complaint.getGrievance_type().equalsIgnoreCase("Emergency")) {
                    lst_complaint.add(complaint);
                    lst_location.add(locationClass);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_adapter);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //Fragment fragment = null;

        if (id == R.id.emergency_requests) {
            SharedPreferences preference = getSharedPreferences("Requests", MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("Requests", "Emergency");
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), AdapterActivity.class);
            startActivity(intent);
        } else if (id == R.id.notifications) {
            SharedPreferences preference = getSharedPreferences("Requests", MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("Requests", "Notification");
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), AdapterActivity.class);
            startActivity(intent);
        } else if (id == R.id.show_unsafe_locations) {
            getUnsafeLocations();
            getUnsafeLocationDetails();
            if (list_location.size() > 0) {
                SharedPreferences preference = getSharedPreferences("Requests", MODE_PRIVATE);
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("Requests", "Unsafe_Location");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), Maps_for_unsafe_location.class);
                intent.putExtra("user_name", user_name);
                intent.putExtra("user_type", user_type);
                intent.putExtra("list_unsafe", (Serializable) list_location);
                startActivity(intent);
            }
        } else if (id == R.id.witness) {
            SharedPreferences preference = getSharedPreferences("Fragment", MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("Fragment", "User");
            editor.commit();
            SharedPreferences preference1 = getSharedPreferences("Login", MODE_PRIVATE);
            SharedPreferences.Editor editor1 = preference1.edit();
            editor1.putString("User_Type", "User");
            editor1.commit();
            navigationView.getMenu().setGroupVisible(R.id.group_witness, false);
            navigationView.getMenu().setGroupVisible(R.id.group_victim, true);
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.witness).setVisible(false);
            nav_menu.findItem(R.id.user).setVisible(true);
            bundle.putString("User_Type", "User");
            Fragment fragment = new VictimLoginFragment();
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        } else if (id == R.id.user) {
            SharedPreferences preference = getSharedPreferences("Fragment", MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("Fragment", "Witness");
            editor.commit();
            SharedPreferences preference1 = getSharedPreferences("Login", MODE_PRIVATE);
            SharedPreferences.Editor editor1 = preference1.edit();
            editor1.putString("User_Type", "Witness");
            editor1.commit();
            navigationView.getMenu().setGroupVisible(R.id.group_victim, false);
            navigationView.getMenu().setGroupVisible(R.id.group_witness, true);
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.witness).setVisible(true);
            nav_menu.findItem(R.id.user).setVisible(false);
            bundle.putString("User_Type", "Witness");
            Fragment fragment = new WitnessLoginFragment();
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        } else if (id == R.id.logout) {
            SharedPreferences preference = getSharedPreferences("Login", MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("User_Type", "");
            editor.putString("User_Name", "");
            editor.commit();
            SharedPreferences preference1 = getSharedPreferences("Fragment", MODE_PRIVATE);
            SharedPreferences.Editor editor1 = preference1.edit();
            editor1.putString("Fragment", "");
            editor1.commit();
            SharedPreferences preference2 = getSharedPreferences("Requests", MODE_PRIVATE);
            SharedPreferences.Editor editor2 = preference2.edit();
            editor2.putString("Requests", "Emergency");
            editor2.commit();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.emergency) {
            GpsTracker g = new GpsTracker(getApplicationContext());
            Location l = g.getLocation();
            if (l != null) {
                victimLoginFragment.sendEmergencyAlert(l, user_name, database_complaints, database_location);
                Toast.makeText(getApplicationContext(), "Your Details along with your Location are sent.....Someone will arrive shortly", Toast.LENGTH_LONG).show();
            } else {
                victimLoginFragment.alert_for_gps();
            }
        } else if (id == R.id.danger) {
        } else if (id == R.id.drive_safe_location) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("latitude", Double.valueOf(safe_latitude));
            intent.putExtra("longitude", Double.valueOf(safe_longitude));
            startActivity(intent);
        } else if (id == R.id.mark_unsafe_by_victim) {
            GpsTracker g = new GpsTracker(getApplicationContext());
            Location l = g.getLocation();
            if (l != null) {
                victimLoginFragment.sendUnsafeLocationAlert(l, database_complaints, database_location);
                Toast.makeText(getApplicationContext(), "This Location is marked unsafe", Toast.LENGTH_LONG).show();
            } else {
                victimLoginFragment.alert_for_gps();
            }
        } else if (id == R.id.generatenewrequests) {
        } else if (id == R.id.trackstatus) {
        } else if (id == R.id.emergency_by_witness) {
            GpsTracker g = new GpsTracker(getApplicationContext());
            Location l = g.getLocation();
            if (l != null) {
                victimLoginFragment.sendEmergencyAlert(l, user_name, database_complaints, database_location);
                Toast.makeText(getApplicationContext(), "Your Details along with your Location are sent.....Someone will arrive shortly", Toast.LENGTH_LONG).show();
            } else {
                victimLoginFragment.alert_for_gps();
            }
        } else if (id == R.id.drive_safe_location_by_witness) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("latitude", Double.valueOf(safe_latitude));
            intent.putExtra("longitude", Double.valueOf(safe_longitude));
            startActivity(intent);
        } else if (id == R.id.mark_unsafe_by_witness) {
            GpsTracker g = new GpsTracker(getApplicationContext());
            Location l = g.getLocation();
            if (l != null) {
                victimLoginFragment.sendUnsafeLocationAlert(l, database_complaints, database_location);
                Toast.makeText(getApplicationContext(), "This Location is marked unsafe", Toast.LENGTH_LONG).show();
            } else {
                victimLoginFragment.alert_for_gps();
            }
        } else if (id == R.id.crime_information) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_adapter);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getNotificationDetails() {
        lst_complaint.clear();
        lst_location.clear();
        lst_location_unsafe.clear();
        if (forComplaint_snapshot != null && forLocation_snapshot != null) {
            for (DataSnapshot complaint_snapshot : forComplaint_snapshot.getChildren()) {
                for (DataSnapshot location_snapshot : forLocation_snapshot.getChildren()) {
                    complaint = complaint_snapshot.getValue(Complaint.class);
                    locationClass = location_snapshot.getValue(LocationClass.class);
                    if (complaint.getComplaint_no().equalsIgnoreCase(locationClass.getComplaint_no()) && complaint.getStatus().equalsIgnoreCase("Sent") && complaint.getEmergency() == true && complaint.getGrievance_type().equalsIgnoreCase("Emergency")) {
                        lst_complaint.add(complaint);
                        lst_location.add(locationClass);
                    }
                }
            }
            for (DataSnapshot complaint_snapshot : forComplaint_snapshot.getChildren()) {
                complaint = complaint_snapshot.getValue(Complaint.class);
                for (DataSnapshot location_snapshot : forLocation_snapshot.getChildren()) {
                    locationClass = location_snapshot.getValue(LocationClass.class);
                    if (complaint.getComplaint_no().equalsIgnoreCase(locationClass.getComplaint_no())) {
                        if (complaint.getGrievance_type().equalsIgnoreCase("Unsafe") && locationClass.getStaus_location().equalsIgnoreCase("Sent"))
                            lst_location_unsafe.add(locationClass);
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Some Error Ocurred", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onStart() {
        getEmergencyRequest();
        getSafeLocation();
        getUnsafeLocations();
        super.onStart();
    }

    @Override
    public void onResume() {
        getEmergencyRequest();
        getUnsafeLocations();
        getSafeLocation();
        super.onResume();
    }

    public void getEmergencyRequest() {
        database_location.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forLocation_snapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        database_complaints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forComplaint_snapshot = dataSnapshot;
                showAllListViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void showAllListViews() {
        if (request_type != null && !request_type.equalsIgnoreCase("")) {
            if (request_type.equalsIgnoreCase("Emergency")) {
                getDetails();
                toolbar.setTitle("Emergency Requests");
                toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                setSupportActionBar(toolbar);
                if (lst_complaint.size() > 0) {
                    ll_emergency_request.setVisibility(View.VISIBLE);
                    lv_emergency_requests.setVisibility(View.VISIBLE);
                    ListAdapter adapter
                            = new ListAdapter(getApplicationContext(), lst_complaint, lst_location);
                    lv_emergency_requests.setAdapter(adapter);
                } else {
                    ll_emergency_request.setVisibility(View.VISIBLE);
                    textview_emergency_request.setVisibility(View.VISIBLE);
                }
            } else if (request_type.equalsIgnoreCase("Notification")) {
                //getDetails();
                getNotificationDetails();
                toolbar.setTitle("Notifications");
                toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                setSupportActionBar(toolbar);
                scrollview_notifications.setVisibility(View.VISIBLE);
                if (lst_complaint.size() > 0) {
                    lv_complaint_notification.setVisibility(View.VISIBLE);
                    ListAdapter adapter
                            = new ListAdapter(getApplicationContext(), lst_complaint, lst_location);
                    lv_complaint_notification.setAdapter(adapter);
                } else {
                    textview_complaint_notification.setVisibility(View.VISIBLE);
                }
                if (lst_location_unsafe.size() > 0) {
                    lv_location_notification.setVisibility(View.VISIBLE);
                    ListLocationNotificationAdapter adapter1
                            = new ListLocationNotificationAdapter(getApplicationContext(), lst_complaint, lst_location_unsafe);
                    lv_location_notification.setAdapter(adapter1);
                } else {
                    textview_location.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return false;
        }
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void getUnsafeLocations() {
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

    public void getUnsafeLocationDetails() {
        if (forLocation_snapshot != null) {
            for (DataSnapshot location_snapshot : forLocation_snapshot.getChildren()) {
                locationClass = location_snapshot.getValue(LocationClass.class);
                if (locationClass.getSafeLocation() == false) {
                    list_location.add(locationClass);
                }
            }
        } else {
            getUnsafeLocations();
            Toast.makeText(getApplicationContext(), "Some Error Ocurred....Press Again", Toast.LENGTH_LONG).show();
        }

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
