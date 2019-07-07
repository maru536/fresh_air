package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;

import com.google.gson.JsonObject;
import com.perfect.freshair.DB.MeasurementDBHandler;

public class Measurement {
    private long timestamp;
    private Dust dust;
    private Location location;

    public Measurement(long timestamp, Dust dust, Location location) {
        this.timestamp = timestamp;
        this.dust = dust;
        this.location = location;
    }

    public Measurement(Cursor cursor) {
        this.timestamp = cursor.getLong(MeasurementDBHandler.Column.TIMESTAMP.ordinal());
        this.dust = new Dust(cursor.getInt(MeasurementDBHandler.Column.PM25.ordinal()), cursor.getInt(MeasurementDBHandler.Column.PM100.ordinal()));
        this.location = new Location(cursor.getString(MeasurementDBHandler.Column.PROVIDER.ordinal()));
        this.location.setAccuracy(cursor.getFloat(MeasurementDBHandler.Column.ACCURACY.ordinal()));
        this.location.setLatitude(cursor.getDouble(MeasurementDBHandler.Column.LATITUDE.ordinal()));
        this.location.setLongitude(cursor.getDouble(MeasurementDBHandler.Column.LONGITUDE.ordinal()));
    }

    public Dust getDust() {
        return dust;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(MeasurementDBHandler.Column.TIMESTAMP.name(), this.timestamp);
        values.put(MeasurementDBHandler.Column.PROVIDER.name(), this.location.getProvider());
        values.put(MeasurementDBHandler.Column.ACCURACY.name(), this.location.getAccuracy());
        values.put(MeasurementDBHandler.Column.LATITUDE.name(), this.location.getLatitude());
        values.put(MeasurementDBHandler.Column.LONGITUDE.name(), this.location.getLongitude());
        values.putAll(this.dust.toValues());

        return values;
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("time", this.timestamp);
        jsonObject.addProperty("pm100", this.dust.getPm100());
        jsonObject.addProperty("pm25", this.dust.getPm25());
        jsonObject.addProperty("provider", this.location.getProvider());
        jsonObject.addProperty("latitude", this.location.getLatitude());
        jsonObject.addProperty("longitude", this.location.getLongitude());

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
