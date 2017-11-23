package com.zainsoft.ramzantimetable;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;


/**
 * Created by Mohsin on 2/8/2017.
 */
public class MyApplication extends MultiDexApplication {
    private static final String TAG = "MyApplication" ;

    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
