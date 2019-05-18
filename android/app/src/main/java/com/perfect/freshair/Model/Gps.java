package com.perfect.freshair.Model;

import android.content.ContentValues;
import android.support.annotation.Nullable;

import com.perfect.freshair.DB.StatusDBHandler;

public class Gps {
    private Position position = null;
    private GpsProvider provider;
    private Satellite sate;
    private float acc = -1.0f;
    private long elapsedTime = -1;
    private PositionStatus positionStatus = PositionStatus.INDOOR;

    public Gps(Position position, GpsProvider provider, float acc, Satellite sate, long elapsedTime, PositionStatus positionStatus) {
        this.position = position;
        this.provider = provider;
        this.acc = acc;
        this.sate = sate;
        this.elapsedTime = elapsedTime;
        this.positionStatus = positionStatus;
    }

    public Gps(GpsProvider provider) {
        this.provider = provider;
    }

    public float getAcc() {
        return acc;
    }

    public Satellite getSate() {
        return sate;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public Position getPosition() {
        return position;
    }

    public GpsProvider getProvider() {
        return provider;
    }

    public PositionStatus getPositionStatus() {
        return positionStatus;
    }

    private boolean isAccNull() {
        return this.acc <= 0.0f;
    }

    private boolean isPositionNull() {
        return this.position == null;
    }

    private boolean isSateNull() {
        return this.sate == null;
    }

    private boolean isElapsedTimeNull() {
        return this.elapsedTime <= 0;
    }

    public String toString() {
        String str = "provider: " +this.provider.name();
        str += ", position Status: " +this.positionStatus.name();

        if (!isPositionNull())
            str += ", Position: (" +this.position.toString()+ ")";

        if (!isAccNull())
            str += ", acc: " +this.acc;

        if (!isSateNull())
            str += ", sate: (" +this.sate.toString()+ ")";

        if (!isElapsedTimeNull())
            str += ", elapsedTime: " +this.elapsedTime;

        return str;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(StatusDBHandler.Column.PROVIDER.name(), this.provider.name());
        values.put(StatusDBHandler.Column.POSITION_STATUS.name(), this.positionStatus.name());

        if (!isPositionNull())
            values.putAll(this.position.toValues());

        if (!isAccNull())
            values.put(StatusDBHandler.Column.ACC.name(), this.acc);

        if (!isSateNull())
            values.putAll(this.sate.toValues());

        if (!isElapsedTimeNull())
            values.put(StatusDBHandler.Column.ELAPSE_TIME.name(), this.elapsedTime);

        return values;
    }
}
