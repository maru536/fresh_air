package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.GnssStatus;

import com.perfect.freshair.DB.StatusDBHandler;

public class Satellite {
    private int totalSate = 0;
    private int useSate = 0;

    public Satellite(int totalSate, int useSate) {
        this.totalSate = totalSate;
        this.useSate = useSate;
    }

    public Satellite(GnssStatus gnssStatus) {
        this.totalSate = gnssStatus.getSatelliteCount();
        for (int i = 0; i < this.totalSate; i++) {
            if (gnssStatus.usedInFix(i))
                this.useSate++;
        }
    }

    public Satellite(Cursor cursor) {
        this.totalSate = cursor.getInt(StatusDBHandler.Column.TOTAL_SATE.ordinal());
        this.useSate = cursor.getInt(StatusDBHandler.Column.USE_SATE.ordinal());
    }

    public int getTotalSate() {
        return this.totalSate;
    }

    public int getUseSate() {
        return this.useSate;
    }

    public String toString() {
        return "totalSate: " +this.totalSate+ ", useSate: " +this.useSate;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(StatusDBHandler.Column.TOTAL_SATE.name(), this.totalSate);
        values.put(StatusDBHandler.Column.USE_SATE.name(), this.useSate);

        return values;
    }
}
