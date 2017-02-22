package com.zainsoft.ramzantimetable.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zainsoft.ramzantimetable.R;

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
}
