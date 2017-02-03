package com.zainsoft.ramzantimetable.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by MB00354042 on 1/24/2017.
 */
public class SalahBootReceiver  extends BroadcastReceiver {
    SalahAlarmReceiver alarm = new SalahAlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            //alarm.setAlarm(context);
        }
    }
}
