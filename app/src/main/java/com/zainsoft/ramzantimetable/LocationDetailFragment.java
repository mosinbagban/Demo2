package com.zainsoft.ramzantimetable;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.zainsoft.ramzantimetable.location.FetchAddressIntentService;
import com.zainsoft.ramzantimetable.network.NetworkConnector;
import com.zainsoft.ramzantimetable.util.Constants;
import com.zainsoft.ramzantimetable.util.DevicePrefernces;
import com.zainsoft.ramzantimetable.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    TextView txtSaher;
    TextView txtIftar;
    TextView txtRamzanCount;
    Button btnLocateMe;
    Switch swAlarm;
    LinearLayout lnrSaher;
    LinearLayout lnrIftar;
    LinearLayout lnrRamzan;

    ListView lstSalah;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
  /*  String lat,lon;
    double latitude;
    double longitude;
    double timezone;*/
    Activity mActivity;
    public static final int INTERVAL = 10000;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static  final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 50;

    private OnFragmentInteractionListener mListener;
    private String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    private Handler mHandler;
    private ProgressDialog pDialog;
    private PrayTime prayers;
    public static String MSG_SHARING_STR;
    public static String ADDRESS = "";
    private DevicePrefernces pref;
    private NotificationManager mNotificationManager;
    String[] locPerms = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    String[] storagePerms = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int locationPermsRequestCode = 100;
    int locationPermsRequestCode1 = 101;
    int storagePermsRequestCode = 200;

    public static Object mLocationMutex;
    private android.support.v7.app.AlertDialog alert;

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
        pref = new DevicePrefernces( getActivity() );
        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel( Constants.NOTIFICATION_ID );

      //  new LocationTasker().execute();
            buildGoogleApiClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location_detail, container, false);
        txtOutputLon = (TextView) rootView.findViewById(R.id.txtLong);
        txtOutputLat = (TextView) rootView.findViewById(R.id.txtLat);
        txtSaher = (TextView) rootView.findViewById( R.id.txtSaherTime );
        txtIftar = (TextView) rootView.findViewById( R.id.txtIftarTime);
        txtRamzanCount = (TextView) rootView.findViewById( R.id.txtRamzanCount );
        txtCity = (TextView) rootView.findViewById(R.id.txtCity);
        lnrIftar = (LinearLayout) rootView.findViewById( R.id.lnrIftar );
        lnrSaher = (LinearLayout) rootView.findViewById( R.id.lnrSaher );
        lnrRamzan = (LinearLayout) rootView.findViewById( R.id.lnrRamzan );
        lstSalah = (ListView) rootView.findViewById(R.id.salahTimeListView);
        btnLocateMe = (Button) rootView.findViewById(R.id.btnLocateMe);
        btnLocateMe.setOnClickListener(this);
        swAlarm = (Switch) rootView.findViewById( R.id.swtchAlarm );
        mLocationMutex = new Object();
       swAlarm.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
               if(isChecked) {
                Utility.setAlarm( getActivity(),1010 );
               } else {

               }
           }
       } );
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );

    }

    @Override
    public void onStart() {
        super.onStart();
        if(pref.getLatitude()!= null || pref.getLongitude()!= null || pref.getTimezone()!= null) {
            double lat = Double.valueOf( pref.getLatitude() );
            double lon = Double.valueOf( pref.getLongitude() );
            double tz = Double.valueOf( pref.getTimezone() );
            updateUI( lat,lon,tz );
        } else {
            buildGoogleApiClient();
            if(hasPermission( locPerms[0] )) {
                settingsRequest();
            } else {
                requestPermissions(locPerms, locationPermsRequestCode1);
            }
        }
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

    class LocationTasker1 extends AsyncTask<Void, Void, Location> {
        @Override
        protected void onPreExecute() {
            if(pDialog == null) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Getting Location...");
            }
            pDialog.show();
            if(!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
            setLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, LocationDetailFragment.this);

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
               double latitude = mLastLocation.getLatitude();
               double longitude = mLastLocation.getLongitude();
               String lat = String.valueOf( latitude );
               String lon = String.valueOf( longitude );
               TimeZone tz = TimeZone.getDefault();
               Log.d(TAG, "TimeZone: " + tz.getDisplayName(false, TimeZone.SHORT) + " : "
                       + tz.getID() + " : "+ tz.getRawOffset());

               double timezone = getTimeZoneVal( tz );
               Log.d(TAG, "tz"+ timezone);
               updateUI( latitude,longitude ,timezone );
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
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mGoogleApiClient.isConnected()) {
            Log.d(TAG, "====onConnected====");
            //new LocationTasker1().execute();
        } else {
            Log.d(TAG, "====NotConnected====");
            buildGoogleApiClient();
            // new LocationTasker().execute();
        }

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

  /*  private void checkPermissions() {
        String permissions[] = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        permission)!= PackageManager.PERMISSION_GRANTED)
                    requestLocationPermission(permissions,ACCESS_FINE_LOCATION_INTENT_ID );
                else
                    settingsRequest();
            }
        } else
            settingsRequest();
    }*/

    private void requestLocationPermission(String[] permissions , int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(),permissions,
                    ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(getActivity(),permissions,
                    ACCESS_FINE_LOCATION_INTENT_ID);
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

    void checkLocPermissionAndExecute() {
        if (hasPermission( locPerms[0] )) {
             new LocationTasker1().execute();
        } else {
            requestPermissions( locPerms, locationPermsRequestCode);
        }
    }

    private void updateUI(Double lat, Double lon, double timezone) {
        Log.d(TAG, "Location received, Updating UI, remove location update to save battery");
        removeLocationUpdate();
      //  txtOutputLat.setText(lat);
      //  txtOutputLon.setText(lon);
        pref.setLatitude(Double.toString( lat ));
        pref.setLongitude(Double.toString( lon ));
        pref.setTimezone( Double.toString( timezone ));
        if(mLastLocation != null)
            startIntentService();

        double[] pTimes = Utility.getSalahTime( timezone,lat ,lon );
        setSalahToAdapter( pTimes );

        if(pref.getAddress() != null) {
            txtCity.setText( pref.getAddress() );
            if(pref.getAddress().contains( "pune" ) || pref.getAddress().contains( "Pune" )) {
                getRamzanTime();
            } else {
                lnrRamzan.setVisibility( View.GONE );
            }
        } else {
            txtCity.setText( pref.getLatlongString() );
        }
    }

    private void getRamzanTime() {
        int tDate = Utility.getRamZanDate();
        if(tDate != -1) {
            lnrRamzan.setVisibility( View.VISIBLE );
            String localDate [] =  getResources().getStringArray(R.array.ramzan_date);
            txtSaher.setText(Constants.SAHERI_TIME[tDate] + " am");
            txtIftar.setText(Constants.IFTAR_TIME[tDate] + " pm");
            txtRamzanCount.setText("" + (tDate + 1));
            if(!pref.isSalahAlarm()) {
                Log.d( TAG, "Setting alarm for Ramzan." );
                boolean isSet = Utility.setRamzanAlarm( getActivity() );
                pref.setSalahAlarm( isSet );
            } else {
                Log.d( TAG, "Alarm already set for Ramzan, no need to set again." );
            }


            // txtRamzanDate.setText(localDate[tDate]);
        } else {
            lnrRamzan.setVisibility( View.GONE );
        }
    }

    private void setSalahToAdapter(double[] pTimes) {
        if(pTimes != null && pTimes.length > 0) {
            prayers = new PrayTime();
            ArrayList<String> prayerNames = prayers.getTimeNames();
            SalahAdapter salahAdapter = new SalahAdapter(getActivity(),pTimes, prayerNames);
            lstSalah.setAdapter(salahAdapter);

            MSG_SHARING_STR = "Prayer times ";
            for(int i=0; i < prayerNames.size(); i++) {
                MSG_SHARING_STR = MSG_SHARING_STR  +" " + prayerNames.get( i ) + "-"
                        + salahAdapter.prayerTimes.get( i ) +" ";
            }
        } else {
            Log.d( TAG, "No times calculated" );
        }

        Log.d( TAG, MSG_SHARING_STR );
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "============OnLocationChanged===========");
        mLastLocation = location;
        if(location != null) {
            Log.d(TAG, "Location received");
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String lat = String.valueOf( latitude );
            String lon = String.valueOf( longitude );
            TimeZone tz = TimeZone.getDefault();
            Log.d(TAG, "TimeZone: " + tz.getDisplayName(false, TimeZone.SHORT) + " : "
                    + tz.getID() + " : "+ tz.getRawOffset());

            double timezone = getTimeZoneVal( tz );
            Log.d(TAG, "tz"+ timezone);
            updateUI( latitude, longitude,timezone );
        } else {
            Log.d(TAG, "mLastLocation is null");
            if(pDialog != null) {
                pDialog.dismiss();
            }
//            new LocationTasker().execute();
            checkLocPermissionAndExecute();
        }
    }

    private void searchLocation() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    private void getPlaces(Intent data) {
        final Place place = PlaceAutocomplete.getPlace(getActivity(), data);
        Log.i(TAG, "Place: " + place.getName());
        Log.d( TAG, "Places :" + place.getLatLng().toString() );
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayAddressOutput(place.getName().toString());
            }
        });

        new GetSalahTimeTasker().execute( place );

    }

    public class GetSalahTimeTasker extends AsyncTask<Place, Void, double[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(pDialog == null) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Getting Salah Time...");
            }
            pDialog.show();
        }

        @Override
        protected double[] doInBackground(Place[] place) {
            double [] prayerTimes = new double[0];
            if(place[0] != null) {
                double latitude = place[0].getLatLng().latitude;
                double longitude = place[0].getLatLng().longitude;
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                String url = "https://maps.googleapis.com/maps/api/timezone/json?location"+
                        "=" +latitude +"," +longitude + "&timestamp="+ts + "&key="+ getString(R.string.timezone_api_key);
                Log.d( TAG, "Url: " + url );
                //String resp = Utility.getRequest(url);
                String resp = null;
                try {
                   resp = NetworkConnector.get( url );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(resp != null) {
                   /* {
                        "dstOffset" : 0,
                            "rawOffset" : -28800,
                            "status" : "OK",
                            "timeZoneId" : "America/Los_Angeles",
                            "timeZoneName" : "Pacific Standard Time"
                    }*/
                    Log.d( TAG, "Resp: " + resp );
                    try {
                        JSONObject jObj = new JSONObject( resp );
                        long rawOffset = jObj.getLong( "rawOffset" );
                        String id = jObj.getString( "timeZoneId" );
                        TimeZone tz = TimeZone.getTimeZone( id );
                        double timezone = getTimeZoneVal( tz );
                        pref.setLatitude( Double.toString( latitude ) );
                        pref.setLongitude( Double.toString( longitude ) );
                        pref.setTimezone( Double.toString( timezone ) );
                        prayerTimes = Utility.getSalahTime(timezone,latitude ,longitude );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return prayerTimes;
        }

        @Override
        protected void onPostExecute(double[] pTimes) {
            super.onPostExecute( pTimes );
           setSalahToAdapter( pTimes );
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
                        searchLocation();
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
            final Address address = (Address)resultData.getParcelable(Constants.RESULT_ADDRESS_KEY);
            if(address != null &&  address.getLocality()!= null ) {
                Log.d(TAG, "Current City: " + address.getLocality());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str = address.getLocality() + ", " + address.getCountryName();
                        displayAddressOutput(str);
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

    private void displayAddressOutput(String address) {
        this.ADDRESS = address;
        Log.d(TAG, "Address: " + MSG_SHARING_STR);
        pref.setAddress( address );
        txtCity.setText(address);
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
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
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
                        Log.d(TAG, "All location settings are satisfied. The client can " +
                                "initialize location requests here.");
                        checkLocPermissionAndExecute();
                      // new LocationTasker().execute();
                       // buildGoogleApiClient();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by
                        // showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            Log.d(TAG, "Show the dialog by calling startResolutionForResult()," +
                                    " and check the result in onActivityResult().");
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Log.d(TAG, "Location settings are not satisfied. However, we have no way" +
                                " to fix the settings so we won't show the dialog.");
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "Received Result OK");
                        if (requestCode == LocationDetailFragment.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                            getPlaces(data);
                            //Log.i(TAG, "Place: " + place.getName());
                        }else if (requestCode == LocationDetailFragment.REQUEST_CHECK_SETTINGS){
//                            new LocationTasker().execute();
                            checkLocPermissionAndExecute();
                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d(TAG, "Received Cancel Request");
                        if (requestCode == LocationDetailFragment.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                            Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                            // TODO: Handle the error.
                            Toast.makeText(getActivity(), "Could not find your location please " +
                                    "try again.", Toast.LENGTH_LONG).show();
                            if(status.getStatusMessage() != null)
                                Log.i(TAG, status.getStatusMessage());

                        }else if (requestCode == LocationDetailFragment.REQUEST_CHECK_SETTINGS){
                            Toast.makeText(getActivity(), "Location setting is disabled, to get " +
                                    "Salah time for your location please enable it.",
                                    Toast.LENGTH_LONG).show();
                        }

                        break;
                }

    }

    /**
     * Check the device is above marshmallow
     *
     * */
    private boolean canMakeSmores(){
        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    private boolean hasPermission(String permission) {
        if(canMakeSmores()){
            return((ContextCompat.checkSelfPermission(getActivity(), permission)==PackageManager.PERMISSION_GRANTED));
        }
        return true;
    }

   /* private void requestPermission(String[] perms, int reqCode) {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(getActivity(), perms, reqCode);
    }*/


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
                Log.d( TAG, ">>>>>>>>>>>>>>>Permission Request Code: " + permsRequestCode  + ">>>>>>>>>>>>>>>>>>>>>>");
        switch(permsRequestCode){

            case 100:
                boolean locationAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
              //  boolean locationAccepted2 = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                Log.d( TAG, "Location Accepted: " + locationAccepted );
               // Log.d( TAG, "Location Accepted2: " + locationAccepted2 );
                if(locationAccepted /*&& locationAccepted2*/) {
                    Log.d( TAG, "===========Location Permission Accepted===============" );
                    new LocationTasker1().execute(  );
                } else {
                    Toast.makeText(getActivity(), "Please allow location permission in order to get your current location", Toast.LENGTH_SHORT).show();
                }
                break;

            case 101:
                boolean locationAccepted1 = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                //  boolean locationAccepted2 = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                Log.d( TAG, "Location Accepted1: " + locationAccepted1 );
                // Log.d( TAG, "Location Accepted2: " + locationAccepted2 );
                if(locationAccepted1 /*&& locationAccepted2*/) {
                    Log.d( TAG, "===========Location Permission Accepted===============" );
                   settingsRequest();
                } else {
                    buildAlertMessageNoGps();
                    //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                }
                break;

            case 200:
                boolean storageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                //do what is required after accepting storage permission
                break;

        }
    }
    private void buildAlertMessageNoGps() {
        if(alert != null && alert.isShowing()) {
            Log.d( TAG, "Already displaying alert" );
        } else {
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setMessage("Kindly allow application to access your location one time in order to get Salah time.")
                    .setCancelable(false)
                    .setPositiveButton("Yes, I'm in", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                            requestPermissions( locPerms, locationPermsRequestCode1 );
                           // startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    })
                    .setNegativeButton("No, I want to Exit", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            //Utility.showToast( getActivity(), "Please allow location permission in order to get your current location" );
                            dialog.cancel();
                            //getActivity().finish();
                        }
                    });
            alert = builder.create();
            alert.show();
        }
    }
}
