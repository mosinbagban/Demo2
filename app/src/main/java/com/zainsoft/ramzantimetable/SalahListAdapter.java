package com.zainsoft.ramzantimetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by MB00354042 on 6/11/2017.
 */
public class SalahListAdapter extends BaseAdapter {

    private final ArrayList<String> prayerNames;

    HashMap<String, HashMap<String, String>> salahCalenderList;
    Context mContext;
    SalahListAdapter(Context context, HashMap<String, HashMap<String, String>> salahCalenderList) {
        this.salahCalenderList = salahCalenderList;
        this.mContext = context;
        PrayTime prayTime = new PrayTime();
        prayerNames = prayTime.getTimeNames();
    }

    @Override
    public int getCount() {
        return salahCalenderList.size();
    }

    @Override
    public Object getItem(int i) {
        return salahCalenderList;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class Holder
    {
        TextView txtDate;
        TextView txtFajr;
        TextView txtSunrise;
        TextView txtDhuhr;
        TextView txtAsr;
        TextView txtSunset;
        TextView txtMaghrib;
        TextView txtIsha;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_row_salah_calender, null);
        holder.txtDate = (TextView) rowView.findViewById( R.id.textDate );
        holder.txtFajr = (TextView) rowView.findViewById( R.id.textFajr );
        holder.txtSunrise = (TextView) rowView.findViewById( R.id.textSunrise );
        holder.txtDhuhr = (TextView) rowView.findViewById( R.id.textDhuhr );
        holder.txtAsr = (TextView) rowView.findViewById( R.id.textAsr );
        holder.txtSunset = (TextView) rowView.findViewById( R.id.textSunset );
        holder.txtMaghrib = (TextView) rowView.findViewById( R.id.textMaghrib );
        holder.txtIsha = (TextView) rowView.findViewById( R.id.textIsha );
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, i);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM");
        HashMap<String, String> daySalah = salahCalenderList.get( dateFormat.format(calendar.getTime()));
        //  Log.d( TAG,  );
        PrayTime prayers = new PrayTime();
        ArrayList<String> prayerNames = prayers.getTimeNames();
        //  for(int j=0; j < prayerNames.size(); j++) {
        // Log.d(TAG, "====" + prayerNames.get( j )+ "::"+ daySalah.get(prayerNames.get( j ))+ "===");
        holder.txtDate.setText( daySalah.get("date") );
        holder.txtFajr.setText( daySalah.get(prayerNames.get( 0 )) );
        holder.txtSunrise.setText( daySalah.get(prayerNames.get( 1 )) );
        holder.txtDhuhr.setText( daySalah.get(prayerNames.get( 2 )) );
        holder.txtAsr.setText( daySalah.get(prayerNames.get( 3 )) );
        holder.txtSunset.setText( daySalah.get(prayerNames.get( 4 )) );
        holder.txtMaghrib.setText( daySalah.get(prayerNames.get( 5 )) );
        holder.txtIsha.setText( daySalah.get(prayerNames.get( 6 )) );
        // }
        return rowView;
    }
}
