
package tech.com.women_protection.Activities;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import tech.com.women_protection.R;
import tech.com.women_protection.classes.LocationClass;

public class Maps_for_unsafe_location extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Bundle bundle;
    List<LocationClass> lst_location = new ArrayList<LocationClass>();
    String user_name, user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_for_unsafe_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map_for_unsafe);
        toolbar.setTitle("Unsafe Locations");
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            user_name = bundle.getString("user_name");
            user_type = bundle.getString("user_type");
            lst_location = (List<LocationClass>) bundle.getSerializable("list_unsafe");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = 13f;
        if (lst_location.size() > 0) {
            for (LocationClass point : lst_location) {
                LatLng location = new LatLng(point.getLatitude(), point.getLongitude());
                mMap.addMarker(new MarkerOptions().position(location).title("Unsafe Locations"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
            }
        }

    }

    @Override
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
        intent.putExtra("User_Name", user_name);
        intent.putExtra("User_Type", user_type);
        startActivity(intent);
        super.onBackPressed();
    }
}
