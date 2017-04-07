package com.zainsoft.ramzantimetable.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zainsoft.ramzantimetable.service.AlarmSetupServices;

/**
 * Created by mb00354042 on 3/16/2017.
 */
public class TimeChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "TimeChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "TimeChange Triggered");
        if(intent.hasExtra( "SalahName" )) {
            Log.d( TAG, "Salah : " + intent.getStringExtra( "SalahName" ) );
            Log.d( TAG, "Time: " + intent.getStringExtra( "SalahTIme" ) );
            start( context, intent );
        }

    }

    public static void start(Context context, Intent intent) {
       // Intent intent = new Intent(context, AlarmSetupServices.class  );
        intent.setClassName(context, "com.zainsoft.ramzantimetable.AlarmSetupServices"  );
        context.startService(intent);
    }
}
