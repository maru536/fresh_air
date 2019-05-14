package com.perfect.freshair.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.perfect.freshair.Callback.TimeoutCallback;
import com.perfect.freshair.Model.GpsSetting;

import java.util.Iterator;

public class GPSUtils {
    private static final String TAG = "GPSUtils";

    public final long TIMEOUT = 5000;
    private static final long WAIT_TERM = 1000;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private GnssStatus.Callback mGnssCallback;
    private TimeoutCallback mTimeoutCallback;
    private long requestTime;
    private TimeoutTask mTimeoutTask;

    public GPSUtils(LocationManager _manager, LocationListener _locationListener, GnssStatus.Callback gnssCallback, TimeoutCallback timeoutCallback) {
        mLocationManager = _manager;
        mLocationListener = _locationListener;
        mGnssCallback = gnssCallback;
        mTimeoutCallback = timeoutCallback;
    }

    private void unregistRequest() {
        mLocationManager.removeUpdates(mLocationListener);
        mLocationManager.unregisterGnssStatusCallback(mGnssCallback);
    }

    @SuppressLint("MissingPermission")
    private void registRequest(int _minUpdateTime, int _minUpdateDist, GpsSetting setting) {
        mLocationManager.registerGnssStatusCallback(mGnssCallback);

        if (setting.isRequestGps()) {
            Log.i(TAG, "Request Gps");
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    _minUpdateTime, _minUpdateDist, mLocationListener, Looper.getMainLooper());

        }

        if (setting.isRequestNetwork()) {
            Log.i(TAG, "Request Network");
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    _minUpdateTime, _minUpdateDist, mLocationListener, Looper.getMainLooper());
        }

        if (setting.isRequestPassive()) {
            Log.i(TAG, "Request Passive");
            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                    _minUpdateTime, _minUpdateDist, mLocationListener, Looper.getMainLooper());
        }
    }

    public void requestGPS(int _minUpdateTime, int _minUpdateDist, GpsSetting setting) {
        unregistRequest();
        registRequest(_minUpdateTime, _minUpdateDist, setting);

        this.requestTime = System.currentTimeMillis();
        mTimeoutTask = new TimeoutTask();
        mTimeoutTask.execute();
    }

    public void stopGPS() {
        unregistRequest();
        mTimeoutTask.cancel(true);
    }

    private class TimeoutTask extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean isTimeout) {
            unregistRequest();

            if (isTimeout)
                mTimeoutCallback.onTimeout();

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
}
