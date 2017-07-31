package com.zainsoft.ramzantimetable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zainsoft.ramzantimetable.util.Constants;
import com.zainsoft.ramzantimetable.util.Utility;

public class RamzanActivity extends AppCompatActivity {

    private TextView txtSaherTime;
    private TextView txtIftarTime;
    private TextView txtRamzanCount;
    private TextView txtRamzanDate;
    private TextView txtSaherIftarMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_ramzan );
        txtSaherTime = (TextView) findViewById(R.id.textSaher);
        txtIftarTime = (TextView) findViewById(R.id.textIftar);
        txtRamzanCount = (TextView) findViewById(R.id.textRamzanNo);
        txtRamzanDate = (TextView) findViewById(R.id.textRamzanDate);
        txtSaherIftarMsg = (TextView) findViewById( R.id.textSahIftarMsg );

        if(getIntent().hasExtra( "saher_iftar_time" )) {
            Utility.cancelNotification( this, 111 );
            txtSaherIftarMsg.setVisibility( View.VISIBLE );
            txtSaherIftarMsg.setText( getIntent().getStringExtra( "saher_iftar_time" ));
        } else {
            txtSaherIftarMsg.setVisibility( View.GONE );
        }
        int tDate = Utility.getRamZanDate();
        if(tDate != -1) {
            String localDate [] =  getResources().getStringArray(R.array.ramzan_date);
            txtSaherTime.setText( Constants.SAHERI_TIME[tDate]);
            txtIftarTime.setText(Constants.IFTAR_TIME[tDate]);
            txtRamzanCount.setText("" + (tDate + 1));
            txtRamzanDate.setText(localDate[tDate]);
        }
    }
}
