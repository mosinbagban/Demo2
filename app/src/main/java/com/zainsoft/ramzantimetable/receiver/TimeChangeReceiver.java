package com.zainsoft.ramzantimetable.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zainsoft.ramzantimetable.service.AlarmSetupServices;
import com.zainsoft.ramzantimetable.util.Utility;

/**
 * Created by mb00354042 on 3/16/2017.
 */
public class TimeChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "TimeChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "TimeChange Triggered");
        start( context, intent );
        //creating notification just to understand it is setting up alarm for next day
        Utility.createNotification(context,"Salah Time Setup","Setting Salah time for next day",100 );
    }

    public static void start(Context context, Intent intent) {
       // Intent intent = new Intent(context, AlarmSetupServices.class  );
        intent.setClassName(context, "com.zainsoft.ramzantimetable.service.AlarmSetupServices"  );
        intent.putExtra( "IsFromTimeChange", true );
        context.startService(intent);
    }
}
