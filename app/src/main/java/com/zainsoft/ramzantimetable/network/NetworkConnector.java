package com.zainsoft.ramzantimetable.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by MB00354042 on 2/8/2017.
 */
public class NetworkConnector {
   // OkHttpClient client;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


  /*  public NetworkConnector() {
         client = new OkHttpClient();
    }
*/

    public static String get(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
