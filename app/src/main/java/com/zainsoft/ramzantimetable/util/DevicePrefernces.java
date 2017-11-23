package com.zainsoft.ramzantimetable.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zainsoft.ramzantimetable.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by MB00354042 on 2/9/2017.
 */
public class DevicePrefernces {

    private Context mContext;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private static  final String USER_ADDRESS = "user_address";
    private static  final String USER_LATITUDE = "user_latitude";
    private static  final String USER_LONGITUDE = "user_longitude";
    private static final String USER_TIMEZONE = "user_timezone";
    private static final String RAMZAN_ALARM = "ramzan_alarm";
    private static final String SALAH_PREF = "salah_pref";
    private String address;
    private String latitude;
    private String longitude;
    private String timezone;


    public DevicePrefernces(Context context) {
        this.mContext = context;
        sharedPref = this.mContext.getSharedPreferences(this.mContext.getString( R.string.my_pref_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void cleanPref() {
        editor.clear();
        editor.commit();
    }

    public String getAddress() {
        return sharedPref.getString( USER_ADDRESS, null );
    }

    public void setAddress(String address) {
        editor.putString(USER_ADDRESS, address);
        editor.commit();
    }

    public String getLatitude() {
        return sharedPref.getString( USER_LATITUDE, null );
    }

    public void setLatitude(String latitude) {
        editor.putString(USER_LATITUDE, latitude);
        editor.commit();
    }

    public String getLongitude() {
        return sharedPref.getString( USER_LONGITUDE, null );
    }

    public void setLongitude(String longitude) {
        editor.putString(USER_LONGITUDE, longitude);
        editor.commit();
    }

    public String getTimezone() {
        return sharedPref.getString( USER_TIMEZONE, null );
    }

    public void setTimezone(String timezone) {
        editor.putString(USER_TIMEZONE, timezone);
        editor.commit();
    }

    public boolean getSalahPref(int i) {
        return sharedPref.getBoolean( SALAH_PREF + i, false );
    }

    public void setSalahPref(boolean isOn, int i) {
        editor.putBoolean(SALAH_PREF +i, isOn);
        editor.commit();
    }

    public boolean isSalahAlarm() {
        return sharedPref.getBoolean( RAMZAN_ALARM , false );
    }

    public void setSalahAlarm(boolean isSet) {
        editor.putBoolean(RAMZAN_ALARM, isSet);
        editor.commit();
    }

    public String getLatlongString() {
        double lat = Double.parseDouble( sharedPref.getString( USER_LATITUDE, "0.0" ));
        double lon = Double.parseDouble( sharedPref.getString( USER_LONGITUDE, "0.0" ));
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode( RoundingMode.CEILING);
        return "(" + df.format(lat) + "," + df.format(lon) + ")";
    }
}
