package tech.com.women_protection;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import tech.com.women_protection.Activities.MapsActivity;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationTitle = "MyApp";
        String message = intent.getStringExtra("message");
        String User_Name = intent.getStringExtra("User_Name");
        String User_Type = intent.getStringExtra("User_Type");
        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");
        if (User_Type != null && !User_Type.equalsIgnoreCase("") && User_Type.equalsIgnoreCase("Admin")) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notification;
            notification = new NotificationCompat.Builder(context);
            notification.setSmallIcon(android.R.drawable.ic_dialog_info);
            notification.setContentTitle("Notification from " + User_Name);
            notification.setContentText("Hi, She is in urgent need..Click Notification to track!");
            notification.setTicker("Urgent Notification");

            Intent intentNotify = new Intent(context, MapsActivity.class);
            intentNotify.putExtra("latitude", Double.valueOf(latitude));
            intentNotify.putExtra("longitude", Double.valueOf(longitude));
            notification.setAutoCancel(true);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentNotify, 0);
            notification.setContentIntent(pendingIntent);
            notificationManager.notify(1, notification.build());
        }
    }
}
