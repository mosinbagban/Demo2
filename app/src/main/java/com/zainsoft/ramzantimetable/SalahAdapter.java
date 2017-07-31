package com.zainsoft.ramzantimetable;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zainsoft.ramzantimetable.receiver.SalahAlarmReceiver;
import com.zainsoft.ramzantimetable.receiver.SampleAlarmReceiver;
import com.zainsoft.ramzantimetable.util.DevicePrefernces;
import com.zainsoft.ramzantimetable.util.Utility;

import java.util.ArrayList;

/**
 * Created by MB00354042 on 1/24/2017.
 */
public class SalahAdapter extends BaseAdapter {

    private static final String TAG = "SalahAdapter";
    double[] pTimes;
    public ArrayList<String> prayerTimes;
    ArrayList<String> prayerNames;
    Context mContext;
    private static LayoutInflater inflater = null;
    PrayTime prayers;
    DevicePrefernces pref;
    private SharedPreferences stoereperfs;


    public SalahAdapter(Context context, double[] pTimes, ArrayList<String>pNames) {
        this.prayerNames = pNames;
        this.pTimes = pTimes;
        this.mContext = context;
        this.inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stoereperfs = PreferenceManager.getDefaultSharedPreferences(context);
        prayers = new PrayTime();
       // Log.d( "Calculation method"+ stoereperfs.getString( "notifications_salah_message", "" ) );
        prayers.setTimeFormat(prayers.Time12);
        prayers.setCalcMethod(prayers.Jafari);
       // prayers.setAsrJuristic(stoereperfs.getString( "","" ));
        prayers.setAdjustHighLats(prayers.AngleBased);
        prayerTimes = prayers.adjustTimesFormat(pTimes);
        pref = new DevicePrefernces( context );
    }
    @Override
    public int getCount() {
        return this.prayerNames.size();
    }

    @Override
    public Object getItem(int i) {
        return this.prayerNames;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    public class Holder
    {
        TextView salahNametv;
        TextView salahTimetv;
        Switch btn;
    }



    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.salah_time_list_row_layout, null);
        holder.salahNametv = (TextView) rowView.findViewById(R.id.txtSalahName);
        holder.salahTimetv = (TextView) rowView.findViewById(R.id.txtSalahTime);
        holder.btn = (Switch) rowView.findViewById(R.id.btnSalahAlarm);
        //Log.d(TAG, i + ". SalahName: " + prayerNames.get(i));
        holder.salahNametv.setText(prayerNames.get(i));
        holder.salahTimetv.setText(prayerTimes.get(i));
        holder.btn.setChecked( pref.getSalahPref( i ) );
        holder.btn.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    setAlarm(prayerNames.get(i) ,pTimes[i], i, false );
                    pref.setSalahPref( isChecked, i );
                    Toast.makeText(mContext, "Reminder set for " + holder.salahNametv.getText(), Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    removeAlarm( mContext, (100+i) );
                    pref.setSalahPref( isChecked, i );
                    Toast.makeText(mContext, "Reminder remove for " + holder.salahNametv.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        } );
//        holder.btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent =  new Intent(  );
//                intent.setClassName(mContext, "com.zainsoft.ramzantimetable.service.AlarmSetupServices"  );
//                intent.putExtra( "salahName", prayerNames.get(i) );
//                intent.putExtra( "salahTime", pTimes[i] );
//                intent.putExtra( "notificationId", i );
//                intent.putExtra("allPrayer", false);
//                mContext.startService(intent);
//                Toast.makeText(mContext, "Reminder set for " + holder.salahNametv.getText(), Toast.LENGTH_SHORT).show();
//            }
//        });

        return rowView;
    }

    private void setAlarm(String salahName, double salahTime, int notificationId, boolean allPrayers){
        Intent intent = new Intent();
        intent.setClassName( mContext, "com.zainsoft.ramzantimetable.service.AlarmSetupServices" );
        intent.putExtra( "salahName", salahName );
        intent.putExtra( "salahTime", salahTime );
        intent.putExtra( "notificationId", notificationId );
        intent.putExtra( "allPrayer", allPrayers );
        intent.putExtra( "IsFromTimeChange", false );
        mContext.startService( intent );
    }

    private void removeAlarm(Context mContext, int requestCode) {
        Intent intent = new Intent(mContext, SalahAlarmReceiver.class);
        intent.setAction( "com.zainsoft.ramzantimetable.SALAH_ALARM" );
        intent.putExtra( "IsFromTimeChange", false );
        PendingIntent alarmIntent = PendingIntent.getBroadcast( mContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT );
        alarmIntent.cancel();
    }
}
