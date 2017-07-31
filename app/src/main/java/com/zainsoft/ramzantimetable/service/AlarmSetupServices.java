package com.zainsoft.ramzantimetable.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zainsoft.ramzantimetable.PrayTime;
import com.zainsoft.ramzantimetable.receiver.SalahAlarmReceiver;
import com.zainsoft.ramzantimetable.util.DevicePrefernces;
import com.zainsoft.ramzantimetable.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mb00354042 on 3/16/2017.
 */
public class AlarmSetupServices extends IntentService {
    private static final String TAG = "AlarmSetupService";

   public AlarmSetupServices() {
       super("AlarmSetupService");
   }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started");
        String dataString = intent.getDataString();
       // Log.d( TAG, "intent: " + dataString );
        getSalahTimesAndSetAlarm();
    }


    private void getSalahTimesAndSetAlarm() {
        Log.d( TAG, "Setting Salah alarm for all prayers" );
        DevicePrefernces pref = new DevicePrefernces( AlarmSetupServices.this );
        if(pref.getLatitude()!= null || pref.getLongitude()!= null || pref.getTimezone()!= null) {
            double lat = Double.valueOf( pref.getLatitude() );
            double lon = Double.valueOf( pref.getLongitude() );
            double tz = Double.valueOf( pref.getTimezone() );
            PrayTime prayers = new PrayTime();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");
            Log.d( TAG, "Alarm setup service date time: " + dateFormat.format(calendar.getTime()));
            double[] pTimes = prayers.getPrayerTimes(calendar, lat, lon, tz );
            ArrayList<String> prayerNames = prayers.getTimeNames();
            prayers.setTimeFormat(prayers.Time12);
            ArrayList<String> displayTime =  prayers.adjustTimesFormat( pTimes);

            for(int i= 0; i < pTimes.length ; i ++ ) {
                Log.d( TAG, prayerNames.get( i ) + "::" + displayTime.get( i ) );
                if(pref.getSalahPref( i )) {
                    Log.d( TAG, "Setting alarm for " + prayerNames.get( i ) );
                    Utility.setSalahAlarm( this, prayerNames.get( i ), pTimes[i],i, false);
                } else {
                    //removing if sny alarm setup earlier
                    Log.d( TAG, "Removing alarm for " + prayerNames.get( i ) );
                    Utility.removeAlarm(i, AlarmSetupServices.this);
                }
            }
        }
    }
}
