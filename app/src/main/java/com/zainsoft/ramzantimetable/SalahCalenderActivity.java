package com.zainsoft.ramzantimetable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zainsoft.ramzantimetable.util.DevicePrefernces;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SalahCalenderActivity extends AppCompatActivity {

    private static final String TAG = "SalahCalenderActivity";
    ListView lstSalahCalender;
    ImageView bmImage;
    LinearLayout lview;
    private HashMap<String, HashMap<String, String>> salahCalenderList;
    private int itemscount;
    private SalahListAdapter adapter;
    private Bitmap bigbitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_salah_calender );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        lstSalahCalender = (ListView) findViewById( R.id.listSalahTimeCalender );

        new SalahCalenderLoader().execute();
        lview = (LinearLayout) findViewById( R.id.lnrcal );
        bmImage = (ImageView) findViewById( R.id.lnrimage );

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make( view, "Share this calender", Snackbar.LENGTH_LONG )
                        .setAction( "Action", null ).show();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bigbitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                        bigbitmap, "Title", null);
                Uri imageUri =  Uri.parse(path);
                sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                sendIntent.setType("image/jpeg");
                startActivity(sendIntent);
            }
        } );


    }

    class SalahCalenderLoader extends AsyncTask {

        @Override
        protected void onPreExecute() {
            Log.d( TAG, "PreExecute..." );
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            return getSalahTimesAndSetAlarm();
        }

        @Override
        protected void onPostExecute(Object result) {
            Log.d( TAG, "PostExecute..." );
            salahCalenderList = (HashMap<String, HashMap<String, String>>) result;
            for (int i = 0; i < salahCalenderList.size(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.add( Calendar.DAY_OF_MONTH, i );
                SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MMM-yyyy" );
                HashMap<String, String> daySalah = salahCalenderList.get( dateFormat.format( calendar.getTime() ) );
                //  Log.d( TAG,  );
                PrayTime prayers = new PrayTime();
                ArrayList<String> prayerNames = prayers.getTimeNames();
                for (int j = 0; j < prayerNames.size(); j++) {
//                   Log.d(TAG, "====" + prayerNames.get( j )+ "::"+ daySalah.get(prayerNames.get( j ))+ "===");
                }
            }
            adapter = new SalahListAdapter( SalahCalenderActivity.this, salahCalenderList );
            lstSalahCalender.setAdapter( adapter );
            setimage();
            super.onPostExecute( result );
        }
    }

    private void setimage() {
       /* lstSalahCalender.setDrawingCacheEnabled(true);
        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null

        lview.measure( View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        lview.layout(0, 0, lview.getMeasuredWidth(), lstSalahCalender.getMeasuredHeight());

        lview.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(lview.getDrawingCache());
        lstSalahCalender.setDrawingCacheEnabled(false); // clear drawing cache
        bmImage.setImageBitmap(b);
       // lstSalahCalender.setVisibility( View.GONE );*/
        int allitemsheight = 0;
        itemscount = adapter.getCount();
        List<Bitmap> bmps = new ArrayList<Bitmap>();
        lview.measure( View.MeasureSpec.makeMeasureSpec(lstSalahCalender.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        lview.layout(0, 0, lview.getMeasuredWidth(), lview.getMeasuredHeight());
        lview.setDrawingCacheEnabled( true );
        lview.buildDrawingCache();
        bmps.add( lview.getDrawingCache() );
        allitemsheight += lview.getMeasuredHeight();
        for (int i = 0; i < itemscount; i++) {
            View childView = adapter.getView( i, null, lstSalahCalender );
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec( lview.getWidth(), View.MeasureSpec.EXACTLY ),
                    View.MeasureSpec.makeMeasureSpec( 0, View.MeasureSpec.UNSPECIFIED ) );
            childView.layout( 0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight() );
            childView.setDrawingCacheEnabled( true );
            childView.buildDrawingCache();
            bmps.add( childView.getDrawingCache() );
            allitemsheight += childView.getMeasuredHeight();
        }

         bigbitmap = Bitmap.createBitmap( lview.getMeasuredWidth(), allitemsheight,
                Bitmap.Config.ARGB_8888 );
        Canvas bigcanvas = new Canvas(bigbitmap);
        Paint paint = new Paint();
        int iHeight = 0;
        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();
            bmp.recycle();
            bmp = null;
        }
        lstSalahCalender.setVisibility( View.GONE );
        lview.setVisibility( View.GONE );
        bmImage.setImageBitmap(bigbitmap);
    }
        private HashMap<String, HashMap<String, String>> getSalahTimesAndSetAlarm() {
            Log.d( TAG, "Setting Salah alarm for all prayers" );
            DevicePrefernces pref = new DevicePrefernces( SalahCalenderActivity.this );
            HashMap<String, HashMap<String, String>> oneDaySalahMapper = new HashMap<>();
            if (pref.getLatitude() != null || pref.getLongitude() != null || pref.getTimezone() != null) {
                double lat = Double.valueOf( pref.getLatitude() );
                double lon = Double.valueOf( pref.getLongitude() );
                double tz = Double.valueOf( pref.getTimezone() );
                PrayTime prayers = new PrayTime();

                for (int i = 0; i < 30; i++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add( Calendar.DAY_OF_MONTH, i );
                    SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MMM" );
                    // Log.d( TAG, "Alarm setup service date time: " + dateFormat.format(calendar.getTime()));
                    double[] pTimes = prayers.getPrayerTimes( calendar, lat, lon, tz );
                    ArrayList<String> prayerNames = prayers.getTimeNames();
                    prayers.setTimeFormat( prayers.Time12 );
                    ArrayList<String> displayTime = prayers.adjustTimesFormat( pTimes );
                    HashMap<String, String> oneDaySalahMap = new HashMap<>();
                    for (int j = 0; j < prayerNames.size(); j++) {
                        oneDaySalahMap.put( prayerNames.get( j ), displayTime.get( j ) );
                    }
                    oneDaySalahMap.put( "date", dateFormat.format( calendar.getTime() ) );
                    oneDaySalahMapper.put( dateFormat.format( calendar.getTime() ), oneDaySalahMap );
                    Log.d( TAG, dateFormat.format( calendar.getTime() ) + "=" + prayerNames.get( 0 ) + ">>"
                            + displayTime.get( 0 ) + " " + prayerNames.get( 5 ) + ">>" + displayTime.get( 5 ) );
                }
            }
            return oneDaySalahMapper;
        }
}
