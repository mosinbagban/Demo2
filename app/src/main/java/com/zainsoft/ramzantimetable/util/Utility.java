package com.zainsoft.ramzantimetable.util;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.zainsoft.ramzantimetable.PrayTime;
import com.zainsoft.ramzantimetable.R;
import com.zainsoft.ramzantimetable.SalahTimeActivity;
import com.zainsoft.ramzantimetable.receiver.SalahAlarmReceiver;
import com.zainsoft.ramzantimetable.receiver.SalahBootReceiver;
import com.zainsoft.ramzantimetable.receiver.TimeChangeReceiver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MB00354042 on 2/2/2017.
 */
public class Utility {

    private static final String TAG = "Utility";


    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getRequest(String urlStr) {
        String response = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            response = readStream(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
            urlConnection.disconnect();
        }
        return response;
    }

    private static String readStream(InputStream in) {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total.toString();
    }

    // BEGIN_INCLUDE(set_alarm)
    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public static void setAlarm(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );

        Intent intent = new Intent(context, TimeChangeReceiver.class);
        intent.setAction( "com.zainsoft.ramzantimetable.TIME_CHANGE" );
        intent.putExtra( "timeChange", "Set new Salah  time @12 am" );
        PendingIntent alarmIntent = PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );

        Calendar calendar = Calendar.getInstance();
        // calendar.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger time to 8:30 a.m.
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE,  47);
        calendar.set(Calendar.SECOND, 00);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        Log.d( TAG, "Alarm Set to 18:47" );

        ComponentName receiver = new ComponentName(context, SalahBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    // BEGIN_INCLUDE(set_alarm)
    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public static void setSalahAlarm(Context context, String salahName, double time, int notificationId) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        int hours = (int)Math.floor(time);
        PrayTime prayers = new PrayTime();
        ArrayList<String> displayTime =  prayers.adjustTimesFormat(new double[]{time});
        double minutes = Math.floor((time - hours) * 60.0);
        Intent intent = new Intent(context, TimeChangeReceiver.class);
        intent.setAction( "com.zainsoft.ramzantimetable.TIME_CHANGE" );
        intent.putExtra( "SalahName", salahName );
        intent.putExtra( "SalahTIme", displayTime.get( 0 ) );

        PendingIntent alarmIntent = PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );

        Calendar calendar = Calendar.getInstance();
        // calendar.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger time to 8:30 a.m.
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE,  (int)minutes);
        calendar.set(Calendar.SECOND, 00);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        Log.d( TAG, "Alarm Set to 18:47" );

        ComponentName receiver = new ComponentName(context, SalahBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static double[] getSalahTime(double timeZone, double latitude, double longitude) {
        Log.d(TAG, "Getting Salah Time");
        PrayTime prayers = new PrayTime();
        prayers.setTimeFormat(prayers.Time24);
        prayers.setCalcMethod(prayers.Jafari);
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        double[] prayerTimes = prayers.getPrayerTimes(cal,latitude, longitude, timeZone);

        return prayerTimes;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void createNotification(Context context, String salahName, String SalahTime) {
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
                .setPriority( NotificationCompat.PRIORITY_MAX)
                .setContentText(SalahTime)
                .setSmallIcon( R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(SalahTime))
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_NO_CLEAR;
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(notificationId, noti);
    }


    public static void setAlarmForSelSalah(Context context, String salahName, double time) {

    }

}
