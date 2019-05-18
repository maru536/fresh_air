package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.database.Cursor;

import com.perfect.freshair.DB.StatusDBHandler;

public class CurrentStatus {
    private long timestamp;
    private Dust dust;
    private Gps gps;

    public CurrentStatus(long timestamp, Dust dust, Gps gps) {
        this.timestamp = timestamp;
        this.dust = dust;
        this.gps = gps;
    }

    public CurrentStatus(Cursor cursor) {
        this.timestamp = cursor.getLong(StatusDBHandler.Column.TIMESTAMP.ordinal());
        this.dust = new Dust(cursor.getInt(StatusDBHandler.Column.PM25.ordinal()), cursor.getInt(StatusDBHandler.Column.PM100.ordinal()));
        Position latestPosition = new Position(cursor.getFloat(StatusDBHandler.Column.LAT.ordinal()), cursor.getFloat(StatusDBHandler.Column.LNG.ordinal()));
        this.gps = new Gps(latestPosition, GpsProvider.fromString(cursor.getString(StatusDBHandler.Column.PROVIDER.ordinal())), cursor.getFloat(StatusDBHandler.Column.ACC.ordinal()),
                new Satellite(cursor),
                cursor.getLong(StatusDBHandler.Column.ELAPSE_TIME.ordinal()), PositionStatus.fromString(cursor.getString(StatusDBHandler.Column.POSITION_STATUS.ordinal())));
    }

    public Dust getDust() {
        return dust;
    }

    public Gps getGps() {
        return gps;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(StatusDBHandler.Column.TIMESTAMP.name(), System.currentTimeMillis());
        values.putAll(this.dust.toValues());
        values.putAll(this.gps.toValues());

        return values;
    }

    public String toString() {
        return timestamp+ ") " +this.dust.toString()+ ", " +this.gps.toString();
    }
}
