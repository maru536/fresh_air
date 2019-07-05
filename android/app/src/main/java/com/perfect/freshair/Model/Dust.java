package com.perfect.freshair.Model;

import android.content.ContentValues;

import com.perfect.freshair.DB.DustMeasurementDBHandler;

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

        values.put(DustMeasurementDBHandler.Column.PM25.name(), this.pm25);
        values.put(DustMeasurementDBHandler.Column.PM100.name(), this.pm100);

        return values;
    }
}
