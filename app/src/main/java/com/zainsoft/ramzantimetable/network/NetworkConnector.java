package com.zainsoft.ramzantimetable.network;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by MB00354042 on 2/8/2017.
 */
public class NetworkConnector {
    private static final String TAG = "NetworkConnector";
    // OkHttpClient client;

    private final String resp = null;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    static OkHttpClient client = new OkHttpClient();

   /* public static  String get(String url) throws IOException {
    
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                ( Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       resp = response.toString();
                        Log.d(TAG, "Response: " + resp);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        resp = null;
                    }
                });


        return resp[0];
    }*/
  /*  public NetworkConnector() {
         client = new OkHttpClient();
    }
*/

    public static String get(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
