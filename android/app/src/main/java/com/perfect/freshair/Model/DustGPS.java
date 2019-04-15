package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.perfect.freshair.DB.DustGPSDBHandler;

import java.sql.Timestamp;

public class DustGPS {
    int mDust;
    Location mCurrentLocation;

    public DustGPS(int dust, Location currentLocation) {
        mDust = dust;
        mCurrentLocation = currentLocation;
    }

    public DustGPS() {
        mDust = -1;
        mCurrentLocation = new Location("");
    }

    public DustGPS(Cursor cursor) {
        this();
        mCurrentLocation.setProvider(cursor.getString(DustGPSDBHandler.Column.PROVIDER.ordinal()));
        mCurrentLocation.setTime(cursor.getLong(DustGPSDBHandler.Column.TIME.ordinal()));
        mCurrentLocation.setElapsedRealtimeNanos(cursor.getLong(DustGPSDBHandler.Column.ELAPSE_TIME.ordinal()));
        mCurrentLocation.setLatitude(cursor.getDouble(DustGPSDBHandler.Column.LAT.ordinal()));
        mCurrentLocation.setLongitude(cursor.getDouble(DustGPSDBHandler.Column.LNG.ordinal()));
        mCurrentLocation.setAltitude(cursor.getDouble(DustGPSDBHandler.Column.ALT.ordinal()));
        mCurrentLocation.setAccuracy(cursor.getFloat(DustGPSDBHandler.Column.ACC.ordinal()));
        mCurrentLocation.setSpeed(cursor.getFloat(DustGPSDBHandler.Column.SPEED.ordinal()));
        mCurrentLocation.setSpeedAccuracyMetersPerSecond(cursor.getFloat(DustGPSDBHandler.Column.SPEED_ACC.ordinal()));
        mCurrentLocation.setVerticalAccuracyMeters(cursor.getFloat(DustGPSDBHandler.Column.VERT_ACC.ordinal()));
        mCurrentLocation.setBearing(cursor.getFloat(DustGPSDBHandler.Column.BEARING.ordinal()));
        mCurrentLocation.setBearingAccuracyDegrees(cursor.getFloat(DustGPSDBHandler.Column.BEARING_ACC.ordinal()));
        mDust = cursor.getInt(DustGPSDBHandler.Column.DUST.ordinal());
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(DustGPSDBHandler.Column.PROVIDER.name(), mCurrentLocation.getProvider());
        values.put(DustGPSDBHandler.Column.TIME.name(), mCurrentLocation.getTime());
        values.put(DustGPSDBHandler.Column.ELAPSE_TIME.name(), mCurrentLocation.getElapsedRealtimeNanos());
        values.put(DustGPSDBHandler.Column.LAT.name(), mCurrentLocation.getLatitude());
        values.put(DustGPSDBHandler.Column.LNG.name(), mCurrentLocation.getLongitude());
        values.put(DustGPSDBHandler.Column.ALT.name(), mCurrentLocation.getAltitude());
        values.put(DustGPSDBHandler.Column.ACC.name(), mCurrentLocation.getAccuracy());
        values.put(DustGPSDBHandler.Column.SPEED.name(), mCurrentLocation.getSpeed());
        values.put(DustGPSDBHandler.Column.SPEED_ACC.name(), mCurrentLocation.getSpeedAccuracyMetersPerSecond());
        values.put(DustGPSDBHandler.Column.VERT_ACC.name(), mCurrentLocation.getVerticalAccuracyMeters());
        values.put(DustGPSDBHandler.Column.BEARING.name(), mCurrentLocation.getBearing());
        values.put(DustGPSDBHandler.Column.BEARING_ACC.name(), mCurrentLocation.getBearingAccuracyDegrees());
        values.put(DustGPSDBHandler.Column.DUST.name(), mDust);

        return values;
    }

    public enum ProviderType {
        GPS, NETWORK, FUSED, ALL;
    }

    public String toString() {
        String str;
        Timestamp time = new Timestamp(mCurrentLocation.getTime());
        str = "Time: " +time.toString()
                + " / Dust: " +mDust
                + " / Provider: " +mCurrentLocation.getProvider()
                + " / Accuracy: " +mCurrentLocation.getAccuracy()
                + " / Lat: " +mCurrentLocation.getLatitude()
                + " / Lng: " +mCurrentLocation.getLongitude();

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

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        JsonObject gps = new JsonObject();

        gps.addProperty("lat", mCurrentLocation.getLatitude());
        gps.addProperty("lng", mCurrentLocation.getLongitude());

        jsonObject.addProperty("dust", mDust);
        jsonObject.add("gps", gps);

        return jsonObject;
    }
}
