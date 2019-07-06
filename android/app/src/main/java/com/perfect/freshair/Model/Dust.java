package com.perfect.freshair.Model;

import android.content.ContentValues;

import com.google.gson.JsonObject;
import com.perfect.freshair.DB.MeasurementDBHandler;

public class Dust {
    private int pm25 = 0;
    private int pm100 = 0;

    public Dust(int pm25, int pm100) {
        this.pm25 = pm25;
        this.pm100 = pm100;
    }

    public int getPm25() {
        return pm25;
    }

    public int getPm100() {
        return pm100;
    }

    public String toString() {
        return "PM2.5: " +this.pm25+ ", PM10: " +this.pm100;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(MeasurementDBHandler.Column.PM25.name(), this.pm25);
        values.put(MeasurementDBHandler.Column.PM100.name(), this.pm100);

        return values;
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("pm100", this.pm100);
        jsonObject.addProperty("pm25", this.pm25);

        return jsonObject;
    }
}
