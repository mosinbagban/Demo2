package com.zainsoft.ramzantimetable;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.zainsoft.ramzantimetable.location.FetchAddressIntentService;
import com.zainsoft.ramzantimetable.util.Constants;
import com.zainsoft.ramzantimetable.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationDetailFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "LocationDetailFragment";
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 100;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView txtOutputLat;
    TextView txtOutputLon;
    TextView txtCity;
    Button btnLocateMe;

    ListView lstSalah;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat,lon;
    double latitude, longitude,timezone;
    Activity mActivity;
    public static final int INTERVAL = 10000;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    private OnFragmentInteractionListener mListener;
    private String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    private Handler mHandler;
    private ProgressDialog pDialog;

    public LocationDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationDetailFragment newInstance(String param1, String param2) {
        LocationDetailFragment fragment = new LocationDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
      //  new LocationTasker().execute();
       // buildGoogleApiClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location_detail, container, false);
        txtOutputLon = (TextView) rootView.findViewById(R.id.txtLong);
        txtOutputLat = (TextView) rootView.findViewById(R.id.txtLat);
        txtCity = (TextView) rootView.findViewById(R.id.txtCity);
        lstSalah = (ListView) rootView.findViewById(R.id.salahTimeListView);
        btnLocateMe = (Button) rootView.findViewById(R.id.btnLocateMe);
        btnLocateMe.setOnClickListener(this);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    class LocationTasker extends AsyncTask<Void, Void, Location> {
        @Override
        protected void onPreExecute() {
            if(pDialog == null) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Getting Location...");
            }
            pDialog.show();
            buildGoogleApiClient();
            setLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, LocationDetailFragment.this);
            super.onPreExecute();
        }

        @Override
        protected Location doInBackground(Void... voids) {
            //buildGoogleApiClient();
           // mGoogleApiClient.connect();
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

       @Override
        protected void onPostExecute(Location loc) {
           super.onPostExecute(loc);
           if(loc != null) {
               mLastLocation = loc;
               latitude = mLastLocation.getLatitude();
               longitude = mLastLocation.getLongitude();
               lat = String.valueOf(latitude);
               lon = String.valueOf(longitude);
               updateUI();
           } else {
               Log.d(TAG, "Location not found");
           }
        }
    }

    synchronized void buildGoogleApiClient() {
       if(mGoogleApiClient == null) {
           mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                   .addConnectionCallbacks(this)
                   .addOnConnectionFailedListener(this)
                   .addApi(LocationServices.API)
                   .build();
       }
       if(!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

    }

    protected void setLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);//10 second
        mLocationRequest.setFastestInterval(5 * 1000);
    }


    protected void removeLocationUpdate() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Removing location update");
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void checkPermissions() {
        String permissions[] = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(),permission)!= PackageManager.PERMISSION_GRANTED)
                    requestLocationPermission(permissions);
                else
                    settingsRequest();
            }
        } else
            settingsRequest();

    }



    private void requestLocationPermission(String[] permissions) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(),permissions, ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(getActivity(),permissions, ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }


    @Override
    public void onResume() {
        //buildGoogleApiClient();
        super.onResume();
    }

    @Override
    public void onStop() {
        removeLocationUpdate();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        removeLocationUpdate();
        super.onDestroy();

    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mGoogleApiClient.isConnected()) {
            Log.d(TAG, "====onConnected====");
          //  new LocationTasker().execute();
            new LocationTasker().execute();
        } else {
            Log.d(TAG, "====NotConnected====");
            buildGoogleApiClient();
           // new LocationTasker().execute();
        }

    }

    private void updateUI() {
        Log.d(TAG, "Location received, Updating UI, remove location update to save battery");
        removeLocationUpdate();
        txtOutputLat.setText(lat);
        txtOutputLon.setText(lon);
        startIntentService();
        getSalahTime();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "============OnLocationChanged===========");
        mLastLocation = location;
        if(location != null) {
            Log.d(TAG, "Location received");
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            lat = String.valueOf(latitude);
            lon = String.valueOf(longitude);
            updateUI();
        } else {
            Log.d(TAG, "mLastLocation is null");
            if(pDialog != null) {
                pDialog.dismiss();
            }
            new LocationTasker().execute();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            buildGoogleApiClient();
    }

    private void showLocationChooser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Please select the Location for Salah Time")
                .setCancelable(true)
                .setPositiveButton("Locate Me", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utility.showToast(getActivity(), "LocateMe");
                        settingsRequest();
                    }
                })
                .setNegativeButton("Search Location", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utility.showToast(getActivity(), "Search Location");
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnLocateMe :
               // settingsRequest();
                showLocationChooser();
                break;
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
            mHandler = handler;
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            final Address address = (Address) resultData.getParcelable(Constants.RESULT_ADDRESS_KEY);
            if(address != null &&  address.getLocality()!= null ) {
                Log.d(TAG, "Current City: " + address.getLocality());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayAddressOutput(address);
                    }
                });
            }


            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
               // showToast(getString(R.string.address_found));
                Log.d(TAG, "Address Found");
            }

        }
    }

    private void displayAddressOutput(Address address) {
        Log.d(TAG, "Address: " + mAddressOutput);
        txtCity.setText(address.getLocality() + ", " + address.getCountryName());
    }

    protected void startIntentService() {
        Log.d(TAG, "Starting Service");
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        mResultReceiver = new AddressResultReceiver(mHandler);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        if(mLastLocation != null) {
            Log.d(TAG, "mlastLocation is not null");
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        }

        getActivity().startService(intent);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getSalahTime() {
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
        TimeZone tz = TimeZone.getDefault();
        Log.d(TAG, "TimeZone: " + tz.getDisplayName(false, TimeZone.SHORT) + " : " + tz.getID() + " : "+ tz.getRawOffset());

        timezone = getTimeZoneVal(tz);
        Log.d(TAG, "tz"+ timezone);
        double[] prayerTimes = prayers.getPrayerTimes(cal,latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();

        SalahAdapter salahAdapter = new SalahAdapter(getActivity(),prayerTimes, prayerNames);
        lstSalah.setAdapter(salahAdapter);
        if(pDialog != null) {
            pDialog.dismiss();
        }
    }

    private static double getTimeZoneVal(TimeZone tz) {
        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                - TimeUnit.HOURS.toMinutes(hours);
        // avoid -4:-30 issue
        minutes = Math.abs(minutes);
        Log.d(TAG, "Hours: " + hours + " min: " + minutes);
        String str = "" + hours + "." + minutes;
        double result = Double.valueOf(str);
        /*if (hours > 0) {
            result = String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID());
        } else {
            result = String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
        }*/
        return result;
    }

    public void settingsRequest() {
        buildGoogleApiClient();
        setLocationRequest();
        Log.d(TAG, "Checking location settings");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        Log.d(TAG, "All location settings are satisfied. The client can initialize location requests here.");
                       new LocationTasker().execute();
                       // buildGoogleApiClient();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            Log.d(TAG, "Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().");
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Log.d(TAG, "Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.");
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "Received Result OK");
                        new LocationTasker().execute();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d(TAG, "Received Cancel Request");
                       // settingsRequest();//keep asking if imp or do whatever
                        Toast.makeText(getActivity(), "Location setting is disabled, to get Salah time for your location please enable it.", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }
}
