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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.zainsoft.ramzantimetable.PrayTime;
import com.zainsoft.ramzantimetable.QiblaActivity;
import com.zainsoft.ramzantimetable.R;
import com.zainsoft.ramzantimetable.SalahTimeActivity;
import com.zainsoft.ramzantimetable.receiver.RamzanAlarmReceiver;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
     * @param s
     * @param pTime
     */
    public static void setAlarm(Context context, int notificationId) {
        Log.d( TAG, "Setting up alarm for time change..." );
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );

        Intent intent = new Intent(context, TimeChangeReceiver.class);
        intent.setAction( "com.zainsoft.ramzantimetable.TIME_CHANGE" );
        intent.putExtra( "timeChange", "Set new Salah  time @12 am" );
        intent.putExtra( "notificationId", notificationId );
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context, (100+notificationId), intent, PendingIntent.FLAG_CANCEL_CURRENT );
        // Set the alarm to start at approximately 2:00 p.m.
        Calendar alarm = Calendar.getInstance();
        alarm.setTimeInMillis(System.currentTimeMillis());
        alarm.set(Calendar.HOUR_OF_DAY, 01);
        alarm.set(Calendar.MINUTE,  27);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");
        Log.d( TAG, "Time  change alarm date time:11: " + dateFormat.format(alarm.getTime()));
        Calendar now = Calendar.getInstance();
          if(alarm.before(now)){
            Log.d( TAG, "Setting alarm for next day>>>" );
           //  timeInMillis = calendar.getTimeInMillis();
            alarm.add(Calendar.DAY_OF_MONTH, 1);
        }

        Log.d( TAG, "Time  change alarm date time:22: " + dateFormat.format(alarm.getTime()));
        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);


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
    public static void setSalahAlarm(Context context, String salahName, double time, int notificationId, boolean isNextDay) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        int hours = (int)Math.floor(time);
        double minutes = Math.floor((time - hours) * 60.0);
        PrayTime prayers = new PrayTime();
        prayers.setTimeFormat(prayers.Time12);
        ArrayList<String> displayTime =  prayers.adjustTimesFormat(new double[]{time});
        Log.d( TAG, "Display Time: " + displayTime.get( 0 ) );
        Intent intent = new Intent(context, SalahAlarmReceiver.class);
        intent.setAction( "com.zainsoft.ramzantimetable.SALAH_ALARM" );
        intent.putExtra( "salahName", salahName );
        intent.putExtra( "salahTIme", displayTime.get( 0 ) );
        intent.putExtra( "notificationId", notificationId );
        PendingIntent alarmIntent = PendingIntent.getBroadcast( context, (100+notificationId), intent, PendingIntent.FLAG_CANCEL_CURRENT );
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");

        Calendar now = Calendar.getInstance();
        Calendar alarm = Calendar.getInstance();
        alarm.set(Calendar.HOUR_OF_DAY, hours);
        alarm.set(Calendar.MINUTE,(int) minutes);
        Log.d( TAG, "Calender for alarm11111: " + dateFormat.format(alarm.getTime()));
       /* if(isNextDay || alarm.before(now)){
            Log.d( TAG, "Setting alarm for next day>>>" );
           //  timeInMillis = calendar.getTimeInMillis();
            alarm.add(Calendar.DAY_OF_MONTH, 1);
        }*/
        long timeInMillis = alarm.getTimeInMillis();
        //System.out.println(dateFormat.format(calendar.getTime()));
        Log.d( TAG, "Calender for alarm22222: " + dateFormat.format(alarm.getTime()));
       // Log.d( TAG, "Calender for alarm: " + calendar.getDisplayName(Calendar.DATE, Calendar.SHORT, Locale.US  ));
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent);
        Log.d( TAG, "Alarm Set to " + hours + ":" + minutes );

        ComponentName receiver = new ComponentName(context, SalahBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void removeAlarm(int index, Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        Intent intent = new Intent(context, SalahAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast( context, 100+index,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.cancel( pendingIntent );
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
    public static void createNotification(Context context, String salahName, String salahTime, int notificationId) {
        // Prepare intent which is triggered if the
        // notification is selected

        Log.d(TAG, "Showing Notification");
        Intent intent = null;
        intent = new Intent(context, SalahTimeActivity.class);
        intent.putExtra("fromNotification",true);
        intent.putExtra( "notificationId", notificationId );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // notificationId = 100;
        DevicePrefernces pref = new DevicePrefernces( context );
        String city = pref.getAddress();
        String bigText = "";
        if(city != null && city.length() > 0) {
            bigText = salahName + " time has been started from " + salahTime + " in " + city;
        }
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(context)
                .setContentTitle(salahName)
                .setPriority( NotificationCompat.PRIORITY_MAX)
                .setContentText(salahTime)
                .setSmallIcon( R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(bigText))
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_NO_CLEAR;
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(notificationId, noti);
    }

    public static void cancelNotification(Context context, int notId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel( notId );
    }


    public static void setAlarmForSelSalah(Context context, String salahName, double time) {

    }

    public static int getRamZanDate() {
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.DAY_OF_MONTH);
        String.format(Locale.US,"%tB",c);
        String tDate =  String.format(Locale.US,"%tB",c) + "-" + seconds;
        for (int i = 0; i < Constants.SAHERI_TIME.length; i++) {
            if(Constants.DATE[i].equalsIgnoreCase(tDate)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean setRamzanAlarm(Context mContext){

        for (int i = 0; i < Constants.SAHERI_TIME.length; i++) {
            Calendar iftarCal = Calendar.getInstance();
            Calendar saherCal = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            int month = 6;
            if(Constants.DATE[i].contains("June")) {
                month = 5;
            }
            String day[] = Constants.DATE[i].split("-");
//			 Log.d(TAG, "Day: " + day.toString());
            String iftarTime [] = Constants.IFTAR_TIME[i].split(":");
            String saherTime [] = Constants.SAHERI_TIME[i].split(":");
//			 Log.d(TAG, "Time: " + time.toString());
            saherCal.set(now.get(Calendar.YEAR), month, Integer.parseInt(day[1]), (Integer.parseInt(saherTime[0])), Integer.parseInt(saherTime[1]), 00);
            iftarCal.set(now.get(Calendar.YEAR), month, Integer.parseInt(day[1]), (12 + Integer.parseInt(iftarTime[0])), Integer.parseInt(iftarTime[1]), 00);
            if(saherCal.compareTo(now) <= 0 ) {
                Log.d(TAG, "Alarm does not set... time already passed " + iftarCal.getTime());
            } else {
                Log.d(TAG,">>>>Alarm is set@>>>> " + saherCal.getTime());
                Intent intent = new Intent(mContext, RamzanAlarmReceiver.class);
                intent.putExtra( "saher_iftar_time", "Today's saheri time has been finished, Now your fasting time started, please recite Dua" );
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, i, intent, 0);
                AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, saherCal.getTimeInMillis(), pendingIntent);
            }

            if(iftarCal.compareTo(now) <= 0 ) {
                Log.d(TAG, "Alarm does not set... time already passed " + iftarCal.getTime());
            } else {
                Log.d(TAG,"\n\n***\n"
                        + "Alarm is set@ " + iftarCal.getTime() + "\n"
                        + "***\n");
                Intent intent = new Intent(mContext, RamzanAlarmReceiver.class);
                intent.putExtra( "saher_iftar_time", "Today's iftar time has been started, you can now release your fast, please recite Dua" );
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, i, intent, 0);
                AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, iftarCal.getTimeInMillis(), pendingIntent);
            }
        }
        return true;
    }

    /**
     * Check the device is above marshmallow
     *
     * */
    public static boolean canMakeSmores(){
        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    public static boolean hasPermission(Context context, String permission) {
        if(canMakeSmores()){
            return((ContextCompat.checkSelfPermission(context, permission)== PackageManager.PERMISSION_GRANTED));
        }
        return true;
    }

}
