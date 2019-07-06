package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.location.Location;

import com.google.gson.JsonObject;
import com.perfect.freshair.DB.MeasurementDBHandler;

public class Gps {
    private double latitude;
    private double longitude;
    private float accuracy;
    private String provider;

    public Gps(String provider, double Latitude, double longitude, float accuracy) {
        this.provider = provider;
        this.latitude = Latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }

    public Gps(Location location) {
        this.provider = location.getProvider();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.accuracy = accuracy;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("latitude", this.latitude);
        jsonObject.addProperty("longitude", this.longitude);

        return jsonObject;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(MeasurementDBHandler.Column.PROVIDER.name(), this.provider);
        values.put(MeasurementDBHandler.Column.LATITUDE.name(), this.latitude);
        values.put(MeasurementDBHandler.Column.LONGITUDE.name(), this.longitude);
        values.put(MeasurementDBHandler.Column.ACCURACY.name(), this.accuracy);

        return values;
    }
}
