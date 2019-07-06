package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.JsonObject;
import com.perfect.freshair.DB.MeasurementDBHandler;

public class Measurement {
    private long timestamp;
    private Dust dust;
    private Gps gps;

    public Measurement(long timestamp, Dust dust, Gps gps) {
        this.timestamp = timestamp;
        this.dust = dust;
        this.gps = gps;
    }

    public Measurement(Cursor cursor) {
        this.timestamp = cursor.getLong(MeasurementDBHandler.Column.TIMESTAMP.ordinal());
        this.dust = new Dust(cursor.getInt(MeasurementDBHandler.Column.PM25.ordinal()), cursor.getInt(MeasurementDBHandler.Column.PM100.ordinal()));
        this.gps = new Gps(cursor.getString(MeasurementDBHandler.Column.PROVIDER.ordinal()), cursor.getDouble(MeasurementDBHandler.Column.LATITUDE.ordinal()),
                cursor.getDouble(MeasurementDBHandler.Column.LONGITUDE.ordinal()), cursor.getFloat(MeasurementDBHandler.Column.ACCURACY.ordinal()));
    }

    public Dust getDust() {
        return dust;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(MeasurementDBHandler.Column.TIMESTAMP.name(), System.currentTimeMillis());
        values.putAll(this.dust.toValues());
        values.putAll(this.gps.toValues());

        return values;
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("time", this.timestamp);
        jsonObject.addProperty("pm100", this.dust.getPm100());
        jsonObject.addProperty("pm25", this.dust.getPm25());
        jsonObject.addProperty("lat", this.gps.getLatitude());
        jsonObject.addProperty("lng", this.gps.getLongitude());

        return jsonObject;
    }

    public String toString() {
        return toJsonObject().toString();
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDust(Dust dust) {
        this.dust = dust;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }
}
