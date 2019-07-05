package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.JsonObject;
import com.perfect.freshair.DB.DustMeasurementDBHandler;

public class DustMeasurement {
    private long timestamp;
    private Dust dust;

    public DustMeasurement(long timestamp, Dust dust) {
        this.timestamp = timestamp;
        this.dust = dust;
    }

    public DustMeasurement(Cursor cursor) {
        this.timestamp = cursor.getLong(DustMeasurementDBHandler.Column.TIMESTAMP.ordinal());
        this.dust = new Dust(cursor.getInt(DustMeasurementDBHandler.Column.PM25.ordinal()), cursor.getInt(DustMeasurementDBHandler.Column.PM100.ordinal()));
    }

    public Dust getDust() {
        return dust;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(DustMeasurementDBHandler.Column.TIMESTAMP.name(), System.currentTimeMillis());
        values.putAll(this.dust.toValues());

        return values;
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("time", this.timestamp);
        jsonObject.addProperty("pm100", this.dust.getPm100());
        jsonObject.addProperty("pm25", this.dust.getPm25());

        return jsonObject;
    }

    public String toString() {
        return toJsonObject().toString();
    }
}
