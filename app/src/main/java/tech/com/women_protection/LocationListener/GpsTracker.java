package tech.com.women_protection.LocationListener;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.widget.Toast;

public class GpsTracker implements LocationListener {
  Context context;
  public GpsTracker(Context c){
    context=c;
  }
  public Location getLocation(){
    LocationManager lm=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    boolean isGpsEnabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    if(isNetworkEnabled){
      try{
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,6000,10,this);
        Location l1=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return l1;
      }catch (SecurityException e){
        e.printStackTrace();
      }
    }
    if(isGpsEnabled){
      try {
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, this);
        Location l=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return l;
      }catch (SecurityException e){
        e.printStackTrace();
      }
    }else{
      Toast.makeText(context,"Please Enable GPS",Toast.LENGTH_LONG).show();
    }
    return null;
  }
  @Override
  public void onLocationChanged(Location location) {

  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }
}
