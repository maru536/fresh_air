package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.perfect.freshair.DB.DustLocationDBHandler;

import java.sql.Timestamp;

public class DustWithLocation {
    int mDust;
    Location mCurrentLocation;

    public DustWithLocation(int dust, Location currentLocation) {
        mDust = dust;
        mCurrentLocation = currentLocation;
    }

    public DustWithLocation() {
        mDust = -1;
        mCurrentLocation = new Location("");
    }

    public DustWithLocation(Cursor cursor) {
        this();
        mCurrentLocation.setProvider(cursor.getString(DustLocationDBHandler.Column.PROVIDER.getIdx()));
        mCurrentLocation.setTime(cursor.getLong(DustLocationDBHandler.Column.TIME.getIdx()));
        mCurrentLocation.setElapsedRealtimeNanos(cursor.getLong(DustLocationDBHandler.Column.ELAPSE_TIME.getIdx()));
        mCurrentLocation.setLatitude(cursor.getDouble(DustLocationDBHandler.Column.LAT.getIdx()));
        mCurrentLocation.setLongitude(cursor.getDouble(DustLocationDBHandler.Column.LNG.getIdx()));
        mCurrentLocation.setAltitude(cursor.getDouble(DustLocationDBHandler.Column.ALT.getIdx()));
        mCurrentLocation.setAccuracy(cursor.getFloat(DustLocationDBHandler.Column.ACC.getIdx()));
        mCurrentLocation.setSpeed(cursor.getFloat(DustLocationDBHandler.Column.SPEED.getIdx()));
        mCurrentLocation.setSpeedAccuracyMetersPerSecond(cursor.getFloat(DustLocationDBHandler.Column.SPEED_ACC.getIdx()));
        mCurrentLocation.setVerticalAccuracyMeters(cursor.getFloat(DustLocationDBHandler.Column.VERT_ACC.getIdx()));
        mCurrentLocation.setBearing(cursor.getFloat(DustLocationDBHandler.Column.BEARING.getIdx()));
        mCurrentLocation.setBearingAccuracyDegrees(cursor.getFloat(DustLocationDBHandler.Column.BEARING_ACC.getIdx()));
        mDust = cursor.getInt(DustLocationDBHandler.Column.DUST.getIdx());
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(DustLocationDBHandler.Column.PROVIDER.name(), mCurrentLocation.getProvider());
        values.put(DustLocationDBHandler.Column.TIME.name(), mCurrentLocation.getTime());
        values.put(DustLocationDBHandler.Column.ELAPSE_TIME.name(), mCurrentLocation.getElapsedRealtimeNanos());
        values.put(DustLocationDBHandler.Column.LAT.name(), mCurrentLocation.getLatitude());
        values.put(DustLocationDBHandler.Column.LNG.name(), mCurrentLocation.getLongitude());
        values.put(DustLocationDBHandler.Column.ALT.name(), mCurrentLocation.getAltitude());
        values.put(DustLocationDBHandler.Column.ACC.name(), mCurrentLocation.getAccuracy());
        values.put(DustLocationDBHandler.Column.SPEED.name(), mCurrentLocation.getSpeed());
        values.put(DustLocationDBHandler.Column.SPEED_ACC.name(), mCurrentLocation.getSpeedAccuracyMetersPerSecond());
        values.put(DustLocationDBHandler.Column.VERT_ACC.name(), mCurrentLocation.getVerticalAccuracyMeters());
        values.put(DustLocationDBHandler.Column.BEARING.name(), mCurrentLocation.getBearing());
        values.put(DustLocationDBHandler.Column.BEARING_ACC.name(), mCurrentLocation.getBearingAccuracyDegrees());
        values.put(DustLocationDBHandler.Column.DUST.name(), mDust);

        return values;
    }

    public enum ProviderType {
        GPS, NETWORK, FUSED, ALL;
    }

    public String toString() {
        String str;
        Timestamp time = new Timestamp(mCurrentLocation.getTime());
        str = "Time: " +time.toString();
        str += " / Lat: " +mCurrentLocation.getLatitude();
        str += " / Lng: " +mCurrentLocation.getLongitude();
        str += " / Dust: " +mDust;

        return str;
    }

    public int getDust() {
        return mDust;
    }

    public void setDust(int dust) {
        mDust = dust;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        mCurrentLocation = currentLocation;
    }

    public LatLng getPosition() {
        return new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
    }
}
