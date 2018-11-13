package tech.com.women_protection;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

public class ReceiveBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationTitle = "MyApp";
        String username = "", usertype = "";
        if (intent.getStringExtra("User_Name") != null && intent.getStringExtra("User_Type") != null) {
            username = intent.getStringExtra("User_Name");
            usertype = intent.getStringExtra("User_Type");
        }
        if (usertype != null && !usertype.equalsIgnoreCase("") && usertype.equalsIgnoreCase("Admin")) {
/*
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setAutoCancel(true)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(notificationTitle)
                    .setContentText(username)
                    .setLights(Color.RED, 1000, 1000)
                    .setVibrate(new long[]{0, 400, 250, 400})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
*/

           /* NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());*/
        }
    }
}
