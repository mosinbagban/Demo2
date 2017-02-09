package com.zainsoft.ramzantimetable;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

/**
 * Created by MB00354042 on 2/8/2017.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication" ;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Initializing Stetho");
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }
}
