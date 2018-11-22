package tech.com.women_protection.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tech.com.women_protection.Activities.MainActivity;
import tech.com.women_protection.Activities.MapsActivity;
import tech.com.women_protection.classes.Complaint;
import tech.com.women_protection.classes.LocationClass;

public class SensorService extends Service {
    public int counter = 0;
    Context applicationContext;
    DatabaseReference database_complaints, database_location;
    DataSnapshot forComplaints_snapshot, forLocation_snapshot;
    String shared_user_type, shared_user_name;
    Complaint complaint;
    LocationClass locationClass;
    String complaint_no, victimName_database;
    double latitude, longitude;
    int request_code = 0, code = 0, requestcode1 = 0;
    List<LocationClass> list = new ArrayList<>();

    public SensorService(Context applicationContext) {
        super();
        this.applicationContext = applicationContext;
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        database_complaints = FirebaseDatabase.getInstance().getReference("Complaints");
        database_location = FirebaseDatabase.getInstance().getReference("Location");
        getAllDatabaseValues();
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("com.tech.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 500, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                code = 0;
                getAllDatabaseValues();
                database_complaints.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        forComplaints_snapshot = dataSnapshot;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                database_location.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        forLocation_snapshot = dataSnapshot;
                        //startTimer();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                SharedPreferences preference = getSharedPreferences("Login", MODE_PRIVATE);
                shared_user_type = preference.getString("User_Type", "");//"No name defined" is the default value.
                shared_user_name = preference.getString("User_Name", "");
                //Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();
                if (shared_user_type != null && !shared_user_type.equalsIgnoreCase("")) {
                    getAllDatabaseValues();
                    if (forComplaints_snapshot != null && forLocation_snapshot != null) {
                        for (DataSnapshot complaint_snapshot : forComplaints_snapshot.getChildren()) {
                            for (DataSnapshot location_snapshot : forLocation_snapshot.getChildren()) {
                                complaint = complaint_snapshot.getValue(Complaint.class);
                                locationClass = location_snapshot.getValue(LocationClass.class);
                                code = code + 1;
                                if (shared_user_type.equalsIgnoreCase("Admin") && complaint.getStatus() != null && !complaint.getStatus().equalsIgnoreCase("")) {
                                    if (complaint.getComplaint_no().equalsIgnoreCase(locationClass.getComplaint_no())) {
                                        if (complaint.getStatus().equalsIgnoreCase("New") && locationClass.getStaus_location().equalsIgnoreCase("New") && complaint.getGrievance_type().equalsIgnoreCase("Emergency")) {
                                            complaint_no = complaint.getComplaint_no();
                                            victimName_database = complaint.getRegistered_by_name();
                                            latitude = locationClass.getLatitude();
                                            longitude = locationClass.getLongitude();
                                            request_code = request_code + 1;
                                            updateAdminDetails(complaint, locationClass, shared_user_name);
                                            Notification_for_Admin(victimName_database, String.valueOf(latitude), String.valueOf(longitude), complaint, locationClass);
                                        } else if (locationClass.getStaus_location().equalsIgnoreCase("New") && complaint.getGrievance_type().equalsIgnoreCase("Unsafe")) {
                                            requestcode1 = requestcode1 + 1;
                                            updateAdminDetails(complaint, locationClass, shared_user_name);
                                            Notification_for_AllUser(String.valueOf(locationClass.getLatitude()), String.valueOf(locationClass.getLongitude()), complaint, locationClass);
                                        }
                                    }
                                }

                            }
                        }
                    } else {
                        getAllDatabaseValues();
                        startTimer();
                    }
                }
/*
                if (list.size() < code) {
                    for (LocationClass classList : list) {
                        for (DataSnapshot location : forLocation_snapshot.getChildren()) {
                            requestcode1 = requestcode1 + 1;
                            locationClass = location.getValue(LocationClass.class);
                            complaint_no = locationClass.getComplaint_no();
                            if (!classList.getComplaint_no().equalsIgnoreCase(complaint_no)) {
                                locationDatasource.insertLocationTable(locationClass);
                                Notification_for_AllUser(String.valueOf(locationClass.getLatitude()), String.valueOf(locationClass.getLongitude()));
                            }
                        }
                    }

                }
*/

            }
        }

        ;
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getAllDatabaseValues() {
        database_complaints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forComplaints_snapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        database_location.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forLocation_snapshot = dataSnapshot;
                //startTimer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void updateAdminDetails(Complaint complaint1, LocationClass locationClass, String user_Name) {
        complaint1.setHandled_by_admin_name(user_Name);
        complaint1.setStatus("Sent");
        locationClass.setStaus_location("Sent");
        database_complaints.child(complaint1.getComplaint_no()).setValue(complaint1);
        database_location.child(complaint1.getComplaint_no()).setValue(locationClass);
    }

   /* public void updateLocationDetails(LocationClass locationClass) {
        locationClass.setStaus_location("Sent");
        database_location.child(locationClass.getComplaint_no()).setValue(locationClass);
    }*/

    public void Notification_for_Admin(String victimName_database, String latitude_database, String longitude_database, Complaint complaint, LocationClass locationClass) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
        mBuilder.setContentTitle("Notification from " + victimName_database);
        mBuilder.setContentText("Hi, She is in urgent need..Click Notification to track!");
        mBuilder.setTicker("Urgent Notification");
        mBuilder.setAutoCancel(true);

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        notificationIntent.putExtra("latitude", Double.valueOf(latitude_database));
        notificationIntent.putExtra("longitude", Double.valueOf(longitude_database));
        notificationIntent.putExtra("complaint", complaint);
        notificationIntent.putExtra("location", locationClass);
        PendingIntent contentIntent = PendingIntent.getActivity(this, request_code, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(request_code, mBuilder.build());
    }

    public void Notification_for_AllUser(String latitude_database, String longitude_database, Complaint complaint1, LocationClass locationClass) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
        mBuilder.setContentTitle("Notification for Unsafe Location");
        mBuilder.setContentText("Click Notification to track!");
        mBuilder.setTicker("Urgent Notification");
        mBuilder.setAutoCancel(true);

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        notificationIntent.putExtra("latitude", Double.valueOf(latitude_database));
        notificationIntent.putExtra("longitude", Double.valueOf(longitude_database));
        notificationIntent.putExtra("complaint", complaint1);
        notificationIntent.putExtra("location", locationClass);
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestcode1, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(requestcode1, mBuilder.build());
    }
}