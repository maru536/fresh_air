package com.perfect.freshair.Model;

import android.content.ContentValues;

import com.perfect.freshair.DB.StatusDBHandler;

public class Gps {
    private Position position;
    private float acc;
    private int totalSate;
    private int useSate;
    private long elapsedTime;

    public Gps(Position position, float acc, int totalSate, int useSate, long elapsedTime) {
        this.position = position;
        this.acc = acc;
        this.totalSate = totalSate;
        this.useSate = useSate;
        this.elapsedTime = elapsedTime;
    }

    public float getAcc() {
        return acc;
    }

    public int getTotalSate() {
        return totalSate;
    }

    public int getUseSate() {
        return useSate;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public Position getPosition() {
        return position;
    }

    public String toString() {
        return this.position.toString()+ ", acc: " +this.acc+ ", total sate: "
                +this.totalSate+ ", use sate: " +this.useSate+ ", elapsedTime: " +this.elapsedTime;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.putAll(this.position.toValues());
        values.put(StatusDBHandler.Column.ACC.name(), this.acc);
        values.put(StatusDBHandler.Column.TOTAL_SATE.name(), this.totalSate);
        values.put(StatusDBHandler.Column.USE_SATE.name(), this.useSate);
        values.put(StatusDBHandler.Column.ELAPSE_TIME.name(), this.elapsedTime);

        return values;
    }
}
