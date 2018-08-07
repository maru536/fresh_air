package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.perfect.freshair.DB.LocationDBHandler;

import java.sql.Timestamp;

public class LocationData {
    public static final String GPS = "gps";
    public static final String NETWORK = "network";
    public static final String ALL = "all";

    LatLng mPos;
    double mAlt;
    float mAcc;
    float mSpeed;
    float mSpeedAcc;
    float mVertAcc;
    float mBearing;
    float mBearingAccDegrees;
    long mElapseRealtime;
    long mTime;
    String mProv;

    public LocationData(Location location) {
        mProv = location.getProvider();
        mTime = location.getTime();
        mElapseRealtime = location.getElapsedRealtimeNanos();
        mPos = new LatLng(location.getLatitude(), location.getLongitude());
        mAlt = location.getAltitude();
        mAcc = location.getAccuracy();
        mSpeed = location.getSpeed();
        mSpeedAcc = location.getSpeedAccuracyMetersPerSecond();
        mVertAcc = location.getVerticalAccuracyMeters();
        mBearing = location.getBearing();
        mBearingAccDegrees = location.getBearingAccuracyDegrees();
    }

    public LocationData(Cursor cursor) {
        mProv = cursor.getString(LocationDBHandler.Column.PROVIDER.getIdx());
        mTime = cursor.getLong(LocationDBHandler.Column.TIME.getIdx());
        mElapseRealtime = cursor.getLong(LocationDBHandler.Column.ELAPSE_TIME.getIdx());
        mPos = new LatLng(cursor.getDouble(LocationDBHandler.Column.LAT.getIdx()),
                    cursor.getDouble(LocationDBHandler.Column.LNG.getIdx()));
        mAlt = cursor.getDouble(LocationDBHandler.Column.ALT.getIdx());
        mAcc = cursor.getFloat(LocationDBHandler.Column.ACC.getIdx());
        mSpeed = cursor.getFloat(LocationDBHandler.Column.SPEED.getIdx());
        mSpeedAcc = cursor.getFloat(LocationDBHandler.Column.SPEED_ACC.getIdx());
        mVertAcc = cursor.getFloat(LocationDBHandler.Column.VERT_ACC.getIdx());
        mBearing = cursor.getFloat(LocationDBHandler.Column.BEARING.getIdx());
        mBearingAccDegrees = cursor.getFloat(LocationDBHandler.Column.BEARING_ACC.getIdx());
    }

    public ContentValues toValues() {
        ContentValues locValues = new ContentValues();

        locValues.put(Key.PROVIDER.getKey(), mProv);
        locValues.put(Key.TIME.getKey(), mTime);
        locValues.put(Key.ELAPSE_TIME.getKey(), mElapseRealtime);
        locValues.put(Key.LAT.getKey(), mPos.latitude);
        locValues.put(Key.LNG.getKey(), mPos.longitude);
        locValues.put(Key.ALT.getKey(), mAlt);
        locValues.put(Key.ACC.getKey(), mAcc);
        locValues.put(Key.SPEED.getKey(), mSpeed);
        locValues.put(Key.SPEED_ACC.getKey(), mSpeedAcc);
        locValues.put(Key.VERT_ACC.getKey(), mVertAcc);
        locValues.put(Key.BEARING.getKey(), mBearing);
        locValues.put(Key.BEARING_ACC.getKey(), mBearingAccDegrees);

        return locValues;
    }

    public LatLng getPos() {
        return mPos;
    }

    public void setPos(LatLng pos) {
        mPos = pos;
    }

    public double getAlt() {
        return mAlt;
    }

    public void setAlt(double alt) {
        mAlt = alt;
    }

    public float getAcc() {
        return mAcc;
    }

    public void setAcc(float acc) {
        mAcc = acc;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public float getSpeedAcc() {
        return mSpeedAcc;
    }

    public void setSpeedAcc(float speedAcc) {
        mSpeedAcc = speedAcc;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public float getVertAcc() {
        return mVertAcc;
    }

    public void setVertAcc(float vertAcc) {
        mVertAcc = vertAcc;
    }

    public float getBearing() {
        return mBearing;
    }

    public void setBearing(float bearing) {
        mBearing = bearing;
    }

    public float getBearingAccDegrees() {
        return mBearingAccDegrees;
    }

    public void setBearingAccDegrees(float bearingAccDegrees) {
        mBearingAccDegrees = bearingAccDegrees;
    }

    public long getElapseRealtime() {
        return mElapseRealtime;
    }

    public void setElapseRealtime(long elapseRealtime) {
        mElapseRealtime = elapseRealtime;
    }

    public String getProv() {
        return mProv;
    }

    public void setProv(String prov) {
        mProv = prov;
    }

    public enum Key {
        LAT("lat"), LNG("lng"), ALT("alt"), ACC("ac"), SPEED("sp"),
        SPEED_ACC("spac"), VERT_ACC("veac"), BEARING("be"), BEARING_ACC("bdac"),
        ELAPSE_TIME("elti"), TIME("ti"), PROVIDER("prov");

        private String mKey;

        private Key(String key) { mKey = key; }

        public String getKey() { return mKey; }
    }

    public String toString() {
        return "Prov: " +mProv+ " / Time: " +new Timestamp(mTime).toString()+ " / Lat: " +mPos.latitude+
                " / Lng: " +mPos.longitude+ " / Alt: " +mAlt+ " / Acc: " +mAcc;
    }
}
