package com.perfect.freshair.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.gson.JsonObject;
import com.perfect.freshair.Callback.GpsCallback;
import com.perfect.freshair.Callback.TimeoutCallback;
import com.perfect.freshair.Model.CurrentStatus;
import com.perfect.freshair.Model.Dust;
import com.perfect.freshair.Model.Gps;
import com.perfect.freshair.Model.GpsProvider;
import com.perfect.freshair.Model.GpsSetting;
import com.perfect.freshair.Model.Position;
import com.perfect.freshair.Model.PositionStatus;
import com.perfect.freshair.Model.Satellite;

public class GpsUtils {
    private static final String TAG = "GpsUtils";

    public final long TIMEOUT = 5000;
    private static final long WAIT_TERM = 1000;
    public static final int MIN_LOCATION_UPDATE_TIME = 0;
    public static final int MIN_LOCATION_UPDATE_DISTANCE = 0;

    private LocationManager locationManager;
    private Location mLocation;
    private boolean mIsGpsDone;
    private GnssStatus mGnssStatus;
    private boolean mIsGnssCallbackDone;
    private boolean mIsTimeout;
    private GpsCallback mGpsCallback;
    private long requestTime;
    private TimeoutTask timeoutTask;
    private int mThresholdAcc = 0;
    private int mThresholdNumSate = 0;
    private int mThresholdTime = 0;
    private int mThresholdTimeout = 0;
    private int mTotalIterate = 0;
    private int mGpsCount = 0;
    private int mTimeoutCount = 0;

    public GpsUtils(Context _appContext) {
        this.locationManager = (LocationManager)_appContext.getSystemService(Context.LOCATION_SERVICE);
        mThresholdAcc = PreferencesUtils.getThresholdAcc(_appContext);
        mThresholdNumSate = PreferencesUtils.getThresholdNumSate(_appContext);
        mThresholdTime = PreferencesUtils.getThresholdElapsedTime(_appContext);
        mThresholdTimeout = PreferencesUtils.getThresholdTimeout(_appContext);
        mTotalIterate = PreferencesUtils.getIterate(_appContext);
    }

    private void init() {
        mGpsCount = 0;
        mTimeoutCount = 0;
    }

    @SuppressLint("MissingPermission")
    private void registRequest() {
        this.requestTime = System.currentTimeMillis();
        this.mGnssStatus = null;
        this.mIsGnssCallbackDone = false;
        this.mLocation = null;
        this.mIsGpsDone = false;
        this.mIsTimeout = false;
        this.locationManager.registerGnssStatusCallback(this.mSatelliteCallback);
        this.timeoutTask = new TimeoutTask();
        this.timeoutTask.execute();
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                this.MIN_LOCATION_UPDATE_TIME, this.MIN_LOCATION_UPDATE_DISTANCE, this.mLocationListener, Looper.getMainLooper());
    }

    private void unregistRequest() {
        this.locationManager.removeUpdates(this.mLocationListener);
        this.locationManager.unregisterGnssStatusCallback(this.mSatelliteCallback);
    }

    public void requestGPS(GpsCallback _gpsCallback) {
        this.init();
        this.mGpsCallback = _gpsCallback;
        registRequest();
    }

    public void stopGPS() {
        unregistRequest();
    }

    private PositionStatus getPoisition(Satellite sate) {
        if (sate.getUseSate() > this.mThresholdNumSate)
            return PositionStatus.OUTDOOR;
        else
            return PositionStatus.INDOOR;
    }

    private class TimeoutTask extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean isTimeout) {
            mIsTimeout = isTimeout;

            if (mIsTimeout)
                onAllGpsDone();
        }

        @Override
        protected void onCancelled(Boolean o) {
            super.onCancelled(o);
        }

        @Override
        protected Boolean doInBackground(Object[] objects) {
            try {
                while (System.currentTimeMillis() - requestTime < TIMEOUT) {
                    Log.i(TAG, "time: " + (System.currentTimeMillis() - requestTime));
                    Thread.sleep(WAIT_TERM);
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                return false;
            }

            return true;
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location _location) {
            mLocation = _location;
            mIsGpsDone = true;

            if (mIsGnssCallbackDone)
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

    private final GnssStatus.Callback mSatelliteCallback = new GnssStatus.Callback() {
        @Override
        public void onSatelliteStatusChanged(GnssStatus gnssStatus) {
            //super.onSatelliteStatusChanged(status);

            mGnssStatus = gnssStatus;
            mIsGnssCallbackDone = true;

            if (mIsGpsDone)
                onAllGpsDone();
        }
    };

    private void onAllGpsDone() {
        unregistRequest();
        Gps gps;

        if (mIsTimeout) {
            gps = new Gps(GpsProvider.TIMEOUT);
            mTimeoutCount++;
        }
        else {
            Satellite sate = new Satellite(mGnssStatus);
            gps = new Gps(new Position(mLocation.getLatitude(), mLocation.getLongitude()), GpsProvider.fromString(mLocation.getProvider()),
                    mLocation.getAccuracy(), sate, System.currentTimeMillis() - this.requestTime, getPoisition(sate));
            mGpsCount++;
        }

        mGpsCallback.onGpsChanged(gps);

        if (mTimeoutCount + mGpsCount < mTotalIterate)
            registRequest();
    }
}
