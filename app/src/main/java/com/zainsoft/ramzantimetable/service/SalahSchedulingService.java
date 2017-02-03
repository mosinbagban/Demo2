package com.zainsoft.ramzantimetable.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.zainsoft.ramzantimetable.R;
import com.zainsoft.ramzantimetable.SalahTimeActivity;
import com.zainsoft.ramzantimetable.receiver.SalahAlarmReceiver;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SalahSchedulingService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.zainsoft.ramzantimetable.service.action.FOO";
    private static final String ACTION_BAZ = "com.zainsoft.ramzantimetable.service.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.zainsoft.ramzantimetable.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.zainsoft.ramzantimetable.service.extra.PARAM2";
    private static final String TAG = "SalahSchedulingService";
    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 1;

    public SalahSchedulingService() {
        super("SalahSchedulingService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SalahSchedulingService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SalahSchedulingService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            String prayerName = intent.getExtras().getString("prayerName");
            String city = intent.getExtras().getString("city");
            Log.d(TAG, "PryaerName: " + prayerName + " City: " + city);
            sendNotification(prayerName, city);
            createNotification(SalahSchedulingService.this, prayerName, "Its " + prayerName + " time started in " + city, "Its " + prayerName + " time started in " + city);
            // Release the wake lock provided by the BroadcastReceiver.
            SalahAlarmReceiver.completeWakefulIntent(intent);
            // END_INCLUDE(service_onhandle)
        }
    }

    private void sendNotification(String prayerName, String city) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SalahTimeActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Salah Time")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Its " + prayerName + " time started in " + city))
                        .setContentText("Its " + prayerName + " time started in " + city);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void createNotification(Context context, String salahName, String salahMsg, String eventMessage) {
        // Prepare intent which is triggered if the
        // notification is selected
        int notificationId = 0;
        Log.d(TAG, "Showing Notification");
        Intent intent = null;

        intent = new Intent(context, SalahTimeActivity.class);
        intent.putExtra("fromNotification",true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationId = 100;
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(context)
                .setContentTitle(salahName)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentText(salahMsg)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(salahMsg))
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_NO_CLEAR;
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(notificationId, noti);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
