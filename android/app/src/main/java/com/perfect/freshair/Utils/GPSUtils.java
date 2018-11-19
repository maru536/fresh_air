package com.perfect.freshair.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.perfect.freshair.Listener.GPSListener;
import com.perfect.freshair.Model.DustWithLocation;
import com.perfect.freshair.View.DustActivity;

public class GPSUtils {
    private static final String TAG = "GPSUtils";

    Context mAppContext;
    private LocationManager mLocationManager;
    private GPSListener mGPSListener;

    public GPSUtils(Context _appContext) {
        mAppContext = _appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void requestGPS(int _minUpdateTime, int _min_update_dist, GPSListener _gpsListener) {
        mGPSListener = _gpsListener;
        mLocationManager.removeUpdates(mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                _minUpdateTime, _min_update_dist, mLocationListener, Looper.getMainLooper());
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                _minUpdateTime, _min_update_dist, mLocationListener, Looper.getMainLooper());
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location _location) {
            mGPSListener.onGPSReceive(_location);
            mLocationManager.removeUpdates(mLocationListener);
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
