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
import com.perfect.freshair.Callback.TimeoutCallback;
import com.perfect.freshair.Model.GpsSetting;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.GPSUtils;
import com.perfect.freshair.Utils.JsonUtils;
import com.perfect.freshair.Utils.PreferencesUtils;

public class MapTestActivity extends NavActivity {
    public static final int MIN_LOCATION_UPDATE_TIME = 0;
    public static final int MIN_LOCATION_UPDATE_DISTANCE = 0;

    private String TAG = "MapTestActivity";
    ImageButton mBtnCurLocation;
    private int mNumSate = -1;
    private int mNumUseSate = -1;
    private int mThresholdAcc = 0;
    private int mThresholdNumSate = 0;
    private int mThresholdTime = 0;
    private int mThresholdTimeout = 0;
    private long mRequestTime = 0;
    private int mTotalIterate = 0;
    private int mCurIterate = 0;
    private int mTimeoutTime = 0;
    private boolean mIsGPSDone = false;
    private boolean mIsSataDone = false;
    private Location mLocation;
    private GPSUtils mGPSUtils;
    private GpsSetting gpsSetting;
    private Context mAppContext;
    private LinearLayout mGpsResultLayout;
    private LayoutInflater mResultInflater;
    private GnssStatus mGnssStatus;
    private JsonArray mGpsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_test);

        mBtnCurLocation = findViewById(R.id.current_location);
        mGpsResultLayout = findViewById(R.id.gps_result);

        mAppContext = getApplicationContext();
        mResultInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mThresholdAcc = PreferencesUtils.getThresholdAcc(mAppContext);
        mThresholdNumSate = PreferencesUtils.getThresholdNumSate(mAppContext);
        mThresholdTime = PreferencesUtils.getThresholdElapsedTime(mAppContext);
        mThresholdTimeout = PreferencesUtils.getThresholdTimeout(mAppContext);
        mTotalIterate = PreferencesUtils.getIterate(mAppContext);

        this.gpsSetting = new GpsSetting(
                PreferencesUtils.getGpsRequest(mAppContext),
                PreferencesUtils.getNetworkRequest(mAppContext),
                PreferencesUtils.getPassiveRequest(mAppContext)
        );

        mBtnCurLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initIter();
                requestCurrentLocation();
            }
        });

        mGPSUtils = new GPSUtils((LocationManager)getSystemService(Context.LOCATION_SERVICE), mLocationListener, satelliteCallback, mTimeoutCallback);
    }

    private void addTextView(String text) {
        TextView textView = (TextView) mResultInflater.inflate(R.layout.gps_result_view, mGpsResultLayout, false);
        textView.setText(text);
        mGpsResultLayout.addView(textView);
    }



    private void requestCurrentLocation() {
        mGPSUtils.requestGPS(MIN_LOCATION_UPDATE_TIME, MIN_LOCATION_UPDATE_DISTANCE, gpsSetting);
        mNumSate = -1;
        mRequestTime = System.currentTimeMillis();
        mIsGPSDone = false;
        mIsSataDone = false;
        this.mLocation = null;
        this.mNumSate = -1;
        this.mNumUseSate = -1;
    }

    private void initIter() {
        mGpsResultLayout.removeAllViews();

        this.mCurIterate = 0;
        this.mTimeoutTime = 0;
        mGpsArray = new JsonArray();
    }


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location _location) {
            mGPSUtils.stopGPS();
            mLocation = _location;
            mIsGPSDone = true;

            if (mIsSataDone)
                onAllGpsDone();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.i(TAG, "onStatusChanged: String) " + s + "/int) " + i + "/bundle) " + bundle.toString());
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.i(TAG, "onProviderEnabled: " + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.i(TAG, "onProviderDisabled: " + s);
        }
    };

    private final GnssStatus.Callback satelliteCallback = new GnssStatus.Callback() {
        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            //super.onSatelliteStatusChanged(status);

            mGnssStatus = status;
            mIsSataDone = true;

            if (mIsGPSDone)
                onAllGpsDone();
        }
    };

    private final TimeoutCallback mTimeoutCallback = new TimeoutCallback() {
        @Override
        public void onTimeout() {
            mCurIterate += 1;
            mTimeoutTime += 1;

            JsonObject gpsInfo = new JsonObject();
            mNumUseSate = 0;
            mNumSate = mGnssStatus.getSatelliteCount();
            JsonArray sateArray = new JsonArray();

            for (int i = 0; i < mNumSate; i++) {
                if (mGnssStatus.usedInFix(i))
                    mNumUseSate++;
            }

            gpsInfo.addProperty("Provider", "timeout");
            if (mGnssStatus != null)
                gpsInfo.add("SateInfoList", JsonUtils.toJsonArray(mGnssStatus));

            mGpsArray.add(gpsInfo);

            String info = "GPS Timeout / Satellite Count: " +mNumSate+ " / Sate User Count: " +mNumUseSate+ " / result: ";

            if (mNumSate >= mThresholdNumSate)
                info += "실외";
            else
                info += "실내";

            addTextView(info);

            if (mCurIterate < mTotalIterate && mTimeoutTime < mThresholdTimeout)
                requestCurrentLocation();
            //mLocationInfo.setText(info);
        }
    };

    private void onAllGpsDone() {
        if (mLocation == null) {
            requestCurrentLocation();
            return;
        }
        long elapsedTime = System.currentTimeMillis() - mRequestTime;
        mCurIterate += 1;

        mNumUseSate = 0;
        mNumSate = mGnssStatus.getSatelliteCount();

        for (int i = 0; i < mNumSate; i++) {
            if (mGnssStatus.usedInFix(i))
                mNumUseSate++;
        }

        JsonObject gpsInfo = JsonUtils.toJsonObject(mLocation);
        gpsInfo.addProperty("Time", elapsedTime);
        gpsInfo.add("SateInfoList", JsonUtils.toJsonArray(mGnssStatus));

        mGpsArray.add(gpsInfo);

        String info = "Provder: " + mLocation.getProvider() + " / Accuracy: " + mLocation.getAccuracy() + " / Satellite Count: " + mNumSate + " / Sate Use Count: " + mNumUseSate + " / time: " + elapsedTime + " / result: ";

        if (mLocation.getAccuracy() <= mThresholdAcc || mNumSate >= mThresholdNumSate || elapsedTime <= mThresholdTime)
            info += "실외";
        else
            info += "실내";

        //mLocationInfo.setText(info);
        addTextView(info);

        if (mCurIterate < mTotalIterate)
            requestCurrentLocation();
    }
}
