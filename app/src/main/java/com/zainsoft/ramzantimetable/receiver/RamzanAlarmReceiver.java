package com.zainsoft.ramzantimetable.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.zainsoft.ramzantimetable.R;

public class RamzanAlarmReceiver extends BroadcastReceiver {
    public RamzanAlarmReceiver() {
    }

  //  private NotificationManager manager;
   // private Notification notification;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Intent in = new Intent(context, RamzanActivity.class);
        String msg = intent.getStringExtra( "saher_iftar_time" );
        intent.setClassName(context, "com.zainsoft.ramzantimetable.RamzanActivity"  );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
/*        PendingIntent Sender = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification( R.mipmap.ic_launcher, "Fasting Time", System.currentTimeMillis());
        notification.setLatestEventInfo(context, "Todays Fast has been Finished ", "You can Release your fast now.", Sender);
        notification.flags = Notification.FLAG_INSISTENT;
        notification.sound = Uri.parse("android.resource://com.zain.holyramazan/raw/alarm_navy_buzzer");
        manager.notify(1, notification);*/

        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(context)
                //.setContentTitle(salahName)
                .setPriority( NotificationCompat.PRIORITY_MAX)
                .setContentText(msg)
                .setSmallIcon( R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_NO_CLEAR;
        noti.sound = Uri.parse("android.resource://com.zain.holyramazan/raw/alarm_navy_buzzer");
       // noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(111, noti);
    }
}
