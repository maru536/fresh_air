package com.perfect.freshair.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.perfect.freshair.Callback.LocationCallback;

public class GpsController {
    private static final String TAG = "GpsController";

    public static final int MIN_LOCATION_UPDATE_TIME = 0;
    public static final int MIN_LOCATION_UPDATE_DISTANCE = 0;

    private LocationManager locationManager;
    private LocationCallback mLocationCallback;

    public GpsController(Context _appContext) {
        this.locationManager = (LocationManager)_appContext.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    private void registRequest() {
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                this.MIN_LOCATION_UPDATE_TIME, this.MIN_LOCATION_UPDATE_DISTANCE, this.mLocationListener, Looper.getMainLooper());

        this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                this.MIN_LOCATION_UPDATE_TIME, this.MIN_LOCATION_UPDATE_DISTANCE, this.mLocationListener, Looper.getMainLooper());
    }

    private void unregistRequest() {
        this.locationManager.removeUpdates(this.mLocationListener);
    }

    public void requestGPS(LocationCallback locationCallback) {
        this.mLocationCallback = locationCallback;
        registRequest();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location _location) {
            unregistRequest();
            mLocationCallback.onLocationChanged(_location);
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
}
