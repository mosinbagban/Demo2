package com.zainsoft.ramzantimetable.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zainsoft.ramzantimetable.PrayTime;
import com.zainsoft.ramzantimetable.util.DevicePrefernces;
import com.zainsoft.ramzantimetable.util.Utility;

import java.util.ArrayList;

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
        Log.d( TAG, "intent: " + dataString );
    }

    private void getSalahTimesAndSetAlarm() {
        DevicePrefernces pref = new DevicePrefernces( AlarmSetupServices.this );
        if(pref.getLatitude()!= null || pref.getLongitude()!= null || pref.getTimezone()!= null) {
            double lat = Double.valueOf( pref.getLatitude() );
            double lon = Double.valueOf( pref.getLongitude() );
            double tz = Double.valueOf( pref.getTimezone() );
            double[] pTimes = Utility.getSalahTime( tz,lat ,lon );
            PrayTime prayers = new PrayTime();
            ArrayList<String> prayerNames = prayers.getTimeNames();
            for(int i= 0; i < pTimes.length ; i ++ ) {

            }
        }

    }

}
