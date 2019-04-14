package com.perfect.freshair.Service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.perfect.freshair.DB.DustGPSDBHandler;
import com.perfect.freshair.Model.DustGPS;

public class GPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String LOGSERVICE = "GPSService";
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private DustGPSDBHandler mLocDB;
    private int mDust;

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        initLocationRequest();
        Log.i(LOGSERVICE, "onCreate");
        mLocDB = new DustGPSDBHandler(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGSERVICE, "onStartCommand");

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOGSERVICE, "onConnected" + bundle);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        android.location.Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        DustGPS newDustGPS = new DustGPS(mDust, location);
        Log.i(LOGSERVICE, newDustGPS.toString());
        mLocDB.add(newDustGPS);
        stopLocationUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOGSERVICE, "onDestroy - Estou sendo destruido ");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOGSERVICE, "onConnectionSuspended " + i);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOGSERVICE, "onConnectionFailed ");

    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void addDustWithLocation(int dust) {
        mDust = dust;
        startLocationUpdate();
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }
}
