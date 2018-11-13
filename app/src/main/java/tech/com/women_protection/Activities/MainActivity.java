package tech.com.women_protection.Activities;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tech.com.women_protection.Fragments.AdminLoginFragment;
import tech.com.women_protection.LocationListener.GpsTracker;
import tech.com.women_protection.R;
import tech.com.women_protection.Fragments.VictimLoginFragment;
import tech.com.women_protection.Fragments.WitnessLoginFragment;
import tech.com.women_protection.SensorService;
import tech.com.women_protection.classes.Complaint;
import tech.com.women_protection.classes.LocationClass;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Bundle bundle;
    String User_Type = "", User_Name = "", complaint_no = "", victimName_database = "";
    DatabaseReference database_complaints, database_location;
    DataSnapshot forLocation_snapshot;
    LocationClass locationClass;
    NavigationView navigationView;
    Intent mServiceIntent;
    private SensorService mSensorService;
    List<LocationClass> list_location = new ArrayList<LocationClass>();
    VictimLoginFragment victimLoginFragment = new VictimLoginFragment();
    Double safe_latitude, safe_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        database_complaints = FirebaseDatabase.getInstance().getReference("Complaints");
        database_location = FirebaseDatabase.getInstance().getReference("Location");

        bundle = getIntent().getExtras();
        if (bundle != null) {
            User_Type = bundle.getString("User_Type");
            User_Name = bundle.getString("User_Name");
        }

        mSensorService = new SensorService(getApplicationContext());
        mServiceIntent = new Intent(getApplicationContext(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
    }

    @Override
    protected void onResume() {
        getUnsafeLocations();
        getSafeLocation();
        ScreenFragment();
        super.onResume();
    }

    @Override
    protected void onStart() {
        getUnsafeLocations();
        getSafeLocation();
        ScreenFragment();
        super.onStart();
    }

    public void ScreenFragment() {
        if (User_Type != null && !User_Type.equalsIgnoreCase("")) {
            if (User_Type.equalsIgnoreCase("Admin")) {
                SharedPreferences preference = getSharedPreferences("Fragment", MODE_PRIVATE);
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("Fragment", "Admin");
                editor.commit();
                navigationView.getMenu().setGroupVisible(R.id.group_admin, true);
                Fragment fragment = new AdminLoginFragment();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.commit();
            } else if (User_Type.equalsIgnoreCase("User")) {
                SharedPreferences preference = getSharedPreferences("Fragment", MODE_PRIVATE);
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("Fragment", "User");
                editor.commit();
                navigationView.getMenu().setGroupVisible(R.id.group_witness, false);
                navigationView.getMenu().setGroupVisible(R.id.group_victim, true);
                Menu nav_menu = navigationView.getMenu();
                nav_menu.findItem(R.id.user).setVisible(true);
                nav_menu.findItem(R.id.witness).setVisible(false);
                Fragment fragment = new VictimLoginFragment();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.commit();
            } else if (User_Type.equalsIgnoreCase("Witness")) {
                SharedPreferences preference = getSharedPreferences("Fragment", MODE_PRIVATE);
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("Fragment", "Witness");
                editor.commit();
                navigationView.getMenu().setGroupVisible(R.id.group_victim, false);
                navigationView.getMenu().setGroupVisible(R.id.group_witness, true);
                Menu nav_menu = navigationView.getMenu();
                nav_menu.findItem(R.id.user).setVisible(false);
                nav_menu.findItem(R.id.witness).setVisible(true);
                Fragment fragment = new WitnessLoginFragment();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.commit();
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
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
            intent.putExtra("user_name", User_Name);
            intent.putExtra("user_type", User_Type);
            startActivity(intent);
        } else if (id == R.id.notifications) {
            SharedPreferences preference = getSharedPreferences("Requests", MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("Requests", "Notification");
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), AdapterActivity.class);
            intent.putExtra("user_name", User_Name);
            intent.putExtra("user_type", User_Type);
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
                intent.putExtra("user_name", User_Name);
                intent.putExtra("user_type", User_Type);
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
                victimLoginFragment.sendEmergencyAlert(l, User_Name, database_complaints, database_location);
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
                victimLoginFragment.sendEmergencyAlert(l, User_Name, database_complaints, database_location);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }


    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

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
