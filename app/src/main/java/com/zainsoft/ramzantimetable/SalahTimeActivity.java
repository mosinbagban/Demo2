package com.zainsoft.ramzantimetable;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.zainsoft.ramzantimetable.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SalahTimeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = "SalahTimeActivity";
    double latitude;
    double longitude;
    double timezone = 5.30;
    TextView txtTime;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    LocationDetailFragment lfrg;
    private SharedPreferences perfs;
    //  private ShareActionProvider myShareActionProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salah_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtTime = (TextView) findViewById(R.id.txtSalahTime);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        perfs = PreferenceManager.getDefaultSharedPreferences(SalahTimeActivity.this);
        Boolean example_switch = perfs.getBoolean( "notifications_salah_message", false );

        Log.d( TAG, "notifications_salah_message: " + example_switch );
        String gpsPerfKey = getString(R.string.gps_pref_key);
        if(getIntent().hasExtra( "notificationId" )) {
            Log.d( TAG, "Application started by notification" );
            int notId = getIntent().getIntExtra( "notificationId", 0 );
            Utility.cancelNotification( this, notId );
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            lfrg = LocationDetailFragment.newInstance("","");
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, lfrg).commit();
        }
       // getSalahTime();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.salah_time, menu);
       // MenuItem shareItem = menu.findItem(R.id.action_share);
       /* myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        myShareActionProvider.setOnShareTargetSelectedListener( this );*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d( TAG, "Options clicked..." );
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent( getApplicationContext(), SettingsActivity.class );
            startActivity( intent );
            return true;
        }else if (id == R.id.action_share) {
            if(lfrg.MSG_SHARING_STR != null) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, lfrg.ADDRESS + " "+ lfrg.MSG_SHARING_STR);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            } else {

            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_qibla) {
            // Handle the camera action
            Intent intent = new Intent( getApplicationContext(), QiblaActivity.class );
            startActivity( intent );
        } else if (id == R.id.nav_salah_cal) {
            Intent intent = new Intent( getApplicationContext(), SalahCalenderActivity.class );
            startActivity( intent );

        } else if (id == R.id.nav_salah_time) {
            lfrg = LocationDetailFragment.newInstance("","");
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, lfrg).commit();
        }/*  else if (id == R.id.nav_manage) {

        } */else if (id == R.id.nav_share) {
            if(lfrg.MSG_SHARING_STR != null) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, lfrg.ADDRESS + " "+ lfrg.MSG_SHARING_STR);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            } else {

            }
        } else if (id == R.id.nav_about) {
           AboutFragment abtFrgmt = AboutFragment.newInstance();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, abtFrgmt).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void doShare(Intent shareIntent) {
        // When you want to share set the share intent.
//        myShareActionProvider.setShareIntent(shareIntent);
    }
    private void getSalahTime() {

        PrayTime prayers = new PrayTime();

        prayers.setTimeFormat(prayers.Time12);
        prayers.setCalcMethod(prayers.Jafari);
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

       /* ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal,
                latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();

        for (int i = 0; i < prayerTimes.size(); i++) {
            System.out.println(prayerNames.get(i) + " - " + prayerTimes.get(i));
            txtTime.append(prayerNames.get(i) + " - " + prayerTimes.get(i) + "\n");
        }*/

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SalahTime Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.zainsoft.ramzantimetable/http/host/path")
        );

    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SalahTime Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.zainsoft.ramzantimetable/http/host/path")
        );
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d(TAG, "OnActivityResult " + requestCode+ " : " + resultCode);
            lfrg.onActivityResult(requestCode,resultCode,data);
            super.onActivityResult(requestCode,resultCode,data );
    }

   /* @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        Log.d( TAG, "Sharing...." );
        doShare(sendIntent  );
        return false;
    }*/
}
