package com.perfect.freshair.Utils;

import android.annotation.SuppressLint;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

public class GPSUtils {
    private static final String TAG = "GPSUtils";

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    public GPSUtils(LocationManager _manager, LocationListener _locationListener) {
        mLocationManager = _manager;
        mLocationListener = _locationListener;
    }

    @SuppressLint("MissingPermission")
    public void requestGPS(int _minUpdateTime, int _minUpdateDist) {
        mLocationManager.removeUpdates(mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                _minUpdateTime, _minUpdateDist, mLocationListener, Looper.getMainLooper());
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                _minUpdateTime, _minUpdateDist, mLocationListener, Looper.getMainLooper());
        mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                _minUpdateTime, _minUpdateDist, mLocationListener, Looper.getMainLooper());
    }

    public void stopGPS() {
        mLocationManager.removeUpdates(mLocationListener);
    }
}
