package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.database.Cursor;

import com.perfect.freshair.DB.StatusDBHandler;

public class CurrentStatus {
    private long timestamp;
    private Dust dust;

    public CurrentStatus(long timestamp, Dust dust) {
        this.timestamp = timestamp;
        this.dust = dust;
    }

    public CurrentStatus(Cursor cursor) {
        this.timestamp = cursor.getLong(StatusDBHandler.Column.TIMESTAMP.ordinal());
        this.dust = new Dust(cursor.getInt(StatusDBHandler.Column.PM25.ordinal()), cursor.getInt(StatusDBHandler.Column.PM100.ordinal()));
    }

    public Dust getDust() {
        return dust;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(StatusDBHandler.Column.TIMESTAMP.name(), System.currentTimeMillis());
        values.putAll(this.dust.toValues());

        return values;
    }

    public String toString() {
        return timestamp+ ") " +this.dust.toString();
    }
}
