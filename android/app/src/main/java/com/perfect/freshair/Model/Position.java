package com.perfect.freshair.Model;

import android.content.ContentValues;

import com.perfect.freshair.DB.StatusDBHandler;

public class Position {
    private double lat;
    private double lng;

    public Position(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String toString() {
        return "lat: " +this.lat+ ", lng: " +this.lng;
    }
    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(StatusDBHandler.Column.LAT.name(), this.lat);
        values.put(StatusDBHandler.Column.LNG.name(), this.lng);

        return values;
    }
}
