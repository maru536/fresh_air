package com.perfect.freshair.Utils;

import android.content.Context;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.perfect.freshair.Callback.TimeoutCallback;
import com.perfect.freshair.DB.StatusDBHandler;
import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Model.Gps;
import com.perfect.freshair.Model.GpsSetting;
import com.perfect.freshair.Model.Position;
import com.perfect.freshair.Model.PositionStatus;

public class GpsService {
    public static final int MIN_LOCATION_UPDATE_TIME = 0;
    public static final int MIN_LOCATION_UPDATE_DISTANCE = 0;
    private String TAG = "GpsService";
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
    private Context appContext;
    private GnssStatus mGnssStatus;
    private JsonArray mGpsArray;

    public GpsService(Context appContext) {
        this.appContext = appContext;

        mThresholdAcc = PreferencesUtils.getThresholdAcc(this.appContext);
        mThresholdNumSate = PreferencesUtils.getThresholdNumSate(this.appContext);
        mThresholdTime = PreferencesUtils.getThresholdElapsedTime(this.appContext);
        mThresholdTimeout = PreferencesUtils.getThresholdTimeout(this.appContext);
        mTotalIterate = PreferencesUtils.getIterate(this.appContext);

        mGPSUtils = new GPSUtils((LocationManager)this.appContext.getSystemService(Context.LOCATION_SERVICE), mLocationListener, satelliteCallback, mTimeoutCallback);
    }

    public void GpsRequest() {
        initIter();
        requestCurrentLocation();
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

            String info = System.currentTimeMillis() +") Gps Timeout / Satellite Count: " +mNumSate+ " / Sate User Count: " +mNumUseSate+ " / result: ";

            CurrentStatus currentStatus;
            if (mNumSate >= mThresholdNumSate) {
                currentStatus = new CurrentStatus(System.currentTimeMillis(), new Dust(10, 20), new Gps(new Position(-1.0, -1.0), -1, mNumSate, mNumUseSate, 5000), PositionStatus.INDOOR);
                info += "실외";
            }
            else {
                currentStatus = new CurrentStatus(System.currentTimeMillis(), new Dust(10, 20), new Gps(new Position(-1.0, -1.0), -1, mNumSate, mNumUseSate, 5000), PositionStatus.INDOOR);
                info += "실내";
            }

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
        CurrentStatus currentStatus;
        String info = System.currentTimeMillis() +") Provder: " + mLocation.getProvider() + " / Accuracy: " + mLocation.getAccuracy() + " / Satellite Count: " + mNumSate + " / Sate Use Count: " + mNumUseSate + " / time: " + elapsedTime + " / result: ";

        if (mLocation.getAccuracy() <= mThresholdAcc || mNumSate >= mThresholdNumSate || elapsedTime <= mThresholdTime) {
            currentStatus = new CurrentStatus(System.currentTimeMillis(), new Dust(10, 20), new Gps(new Position(mLocation.getLatitude(), mLocation.getLongitude()), mLocation.getAccuracy(), mNumSate, mNumUseSate, elapsedTime), PositionStatus.OUTDOOR);
            info += "실외";
        }
        else {
            currentStatus = new CurrentStatus(System.currentTimeMillis(), new Dust(10, 20), new Gps(new Position(mLocation.getLatitude(), mLocation.getLongitude()), mLocation.getAccuracy(), mNumSate, mNumUseSate, elapsedTime), PositionStatus.INDOOR);
            info += "실내";
        }

        if (mCurIterate < mTotalIterate)
            requestCurrentLocation();
    }
}
