package com.zainsoft.ramzantimetable;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zainsoft.ramzantimetable.receiver.SalahAlarmReceiver;
import com.zainsoft.ramzantimetable.receiver.SampleAlarmReceiver;
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



    public SalahAdapter(Context context, double[] pTimes, ArrayList<String>pNames) {
        this.prayerNames = pNames;
        this.pTimes = pTimes;
        this.mContext = context;
        this.inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        prayers = new PrayTime();
        prayers.setTimeFormat(prayers.Time12);
        prayers.setCalcMethod(prayers.Jafari);
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        prayerTimes = prayers.adjustTimesFormat(pTimes);
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
        Button btn;
    }



    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.salah_time_list_row_layout, null);
        holder.salahNametv = (TextView) rowView.findViewById(R.id.txtSalahName);
        holder.salahTimetv = (TextView) rowView.findViewById(R.id.txtSalahTime);
        holder.btn = (Button) rowView.findViewById(R.id.btnSalahAlarm);
        //Log.d(TAG, i + ". SalahName: " + prayerNames.get(i));
        holder.salahNametv.setText(prayerNames.get(i));
        holder.salahTimetv.setText(prayerTimes.get(i));
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* SampleAlarmReceiver alarm = new SampleAlarmReceiver();
                alarm.setAlarm(mContext);*/
                Utility.setAlarm( mContext );
                //set the alarm by using salahTimetv value
                /*SalahAlarmReceiver alarm = new SalahAlarmReceiver();
                alarm.setAlarm(mContext, pTimes[i], prayerNames.get(i), "");
                Toast.makeText(mContext, "Reminder set for " + holder.salahNametv.getText(), Toast.LENGTH_SHORT).show();*/
            }
        });
        return rowView;
    }
}
