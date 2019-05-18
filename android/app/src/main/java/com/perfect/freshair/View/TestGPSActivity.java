package com.perfect.freshair.View;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.perfect.freshair.Callback.GpsCallback;
import com.perfect.freshair.DB.StatusDBHandler;
import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Model.Gps;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.GpsUtils;

public class TestGPSActivity extends AppCompatActivity {
    public static final int MIN_LOCATION_UPDATE_TIME = 0;
    public static final int MIN_LOCATION_UPDATE_DISTANCE = 0;

    private String TAG = "MapTestActivity";
    ImageButton mBtnCurLocation;
    private GpsUtils mGpsUtils;
    private LinearLayout mGpsResultLayout;
    private LayoutInflater mResultInflater;
    private StatusDBHandler statusDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gps);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtnCurLocation = findViewById(R.id.current_location);
        mGpsResultLayout = findViewById(R.id.gps_result);
        this.statusDBHandler = new StatusDBHandler(this);

        mResultInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mGpsUtils = new GpsUtils(getApplicationContext());

        mBtnCurLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGpsResultLayout.removeAllViews();
                CurrentStatus status = statusDBHandler.latestRow();

                if (status != null)
                    addTextView(status.toString());
                else
                    addTextView("Empty!!!");

                mGpsUtils.requestGPS(mGpsCallback);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void addTextView(String text) {
        TextView textView = (TextView) mResultInflater.inflate(R.layout.gps_result_view, mGpsResultLayout, false);
        textView.setText(text);
        textView.setTextColor(R.color.black);
        mGpsResultLayout.addView(textView);
    }

    private GpsCallback mGpsCallback = new GpsCallback() {
        @Override
        public void onGpsChanged(Gps gps) {
            CurrentStatus status = new CurrentStatus(System.currentTimeMillis(), new Dust(10, 20), gps);
            statusDBHandler.add(status);
            addTextView(status.toString());
        }
    };
}
