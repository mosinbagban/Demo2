package com.zainsoft.ramzantimetable.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.zainsoft.ramzantimetable.PrayTime;
import com.zainsoft.ramzantimetable.service.SalahSchedulingService;
import com.zainsoft.ramzantimetable.util.DevicePrefernces;
import com.zainsoft.ramzantimetable.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MB00354042 on 1/24/2017.
 */
public class SalahAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "SalahAlarmReceiver";
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    private String salahName;
    private String salahTime;
    private int notificationId;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm triggered from receiver...");
        this.mContext = context;
        salahName = intent.getStringExtra( "salahName" );
        salahTime = intent.getStringExtra( "salahTIme" );
        int notificationId = intent.getIntExtra( "notificationId", 0 );
        Log.d( TAG, "Salah : " + salahName );
        Log.d( TAG, "Time: " + salahTime );
        Utility.createNotification( context,salahName,salahTime, notificationId );
        getSalahTimesAndSetAlarm(notificationId);
     }

    private void getSalahTimesAndSetAlarm(int salahIndex) {
        Log.d( TAG, "Setting Salah alarm for all prayers" );
        DevicePrefernces pref = new DevicePrefernces(mContext);
        if(pref.getLatitude()!= null || pref.getLongitude()!= null || pref.getTimezone()!= null) {
            double lat = Double.valueOf( pref.getLatitude() );
            double lon = Double.valueOf( pref.getLongitude() );
            double tz = Double.valueOf( pref.getTimezone() );
            PrayTime prayers = new PrayTime();
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");
            //System.out.println(dateFormat.format(calendar.getTime()));
            Log.d( TAG, "Calender for pray time: " + dateFormat.format(calendar.getTime()));
            double[] pTimes = prayers.getPrayerTimes(calendar, lat, lon, tz );
            ArrayList<String> prayerNames = prayers.getTimeNames();
            Utility.setSalahAlarm( mContext, prayerNames.get( salahIndex ), pTimes[salahIndex],salahIndex, false);
        }
    }
}
