package com.perfect.freshair.Callback;

import android.location.Location;

import com.perfect.freshair.Model.Gps;

public interface GpsCallback {
    void onGpsChanged(Gps gps);
}
