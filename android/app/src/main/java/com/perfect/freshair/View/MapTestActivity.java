package com.perfect.freshair.View;

import android.content.Context;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.perfect.freshair.Callback.GpsCallback;
import com.perfect.freshair.Callback.TimeoutCallback;
import com.perfect.freshair.DB.StatusDBHandler;
import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Model.Gps;
import com.perfect.freshair.Model.GpsProvider;
import com.perfect.freshair.Model.GpsSetting;
import com.perfect.freshair.Model.Position;
import com.perfect.freshair.Model.PositionStatus;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.GPSUtils;
import com.perfect.freshair.Utils.JsonUtils;
import com.perfect.freshair.Utils.PreferencesUtils;

public class MapTestActivity extends NavActivity {
    public static final int MIN_LOCATION_UPDATE_TIME = 0;
    public static final int MIN_LOCATION_UPDATE_DISTANCE = 0;

    private String TAG = "MapTestActivity";
    ImageButton mBtnCurLocation;
    private GPSUtils mGPSUtils;
    private LinearLayout mGpsResultLayout;
    private LayoutInflater mResultInflater;
    private StatusDBHandler statusDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_test);

        mBtnCurLocation = findViewById(R.id.current_location);
        mGpsResultLayout = findViewById(R.id.gps_result);
        this.statusDBHandler = new StatusDBHandler(this);

        mResultInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mGPSUtils = new GPSUtils(getApplicationContext());

        mBtnCurLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGPSUtils.requestGPS(mGpsCallback);
            }
        });

    }

    private void addTextView(String text) {
        TextView textView = (TextView) mResultInflater.inflate(R.layout.gps_result_view, mGpsResultLayout, false);
        textView.setText(text);
        textView.setTextColor(R.color.transparentBlack);
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
