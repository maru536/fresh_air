package com.perfect.freshair.Model;

import com.google.gson.JsonObject;

public class LatestDust {
    private long time;
    private int pm100;
    private int pm25;

    public LatestDust(long time, int pm100, int pm25) {
        this.time = time;
        this.pm100 = pm100;
        this.pm25 = pm25;
    }

    public LatestDust(long time, Dust dust) {
        this.time = time;
        this.pm100 = dust.getPm100();
        this.pm25 = dust.getPm25();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPm100() {
        return pm100;
    }

    public void setPm100(int pm100) {
        this.pm100 = pm100;
    }

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("time", this.time);
        jsonObject.addProperty("pm100", this.pm100);
        jsonObject.addProperty("pm25", this.pm25);

        return jsonObject;
    }
}
