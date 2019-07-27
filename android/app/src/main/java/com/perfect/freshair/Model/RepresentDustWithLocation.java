package com.perfect.freshair.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.perfect.freshair.Utils.JsonUtils;

public class RepresentDustWithLocation {
    private long latestTime;
    private Dust dust;
    private LatLng position;

    public RepresentDustWithLocation(long latestTime, Dust dust, LatLng position) {
        this.latestTime = latestTime;
        this.dust = dust;
        this.position = position;
    }

    public RepresentDustWithLocation(JsonObject representDustWithLocation) {
        latestTime = JsonUtils.getAsLong(representDustWithLocation.get(Key.LATEST_TIME.getKey()), -1L);
        dust = new Dust(JsonUtils.getAsJsonObject(representDustWithLocation.get(Key.DUST.getKey())));
        JsonObject center = JsonUtils.getAsJsonObject(representDustWithLocation.get(Key.POSITION.getKey()));
        position = new LatLng(JsonUtils.getAsDouble(center.get(Key.LATITUDE.getKey()), 0.0),
                    JsonUtils.getAsDouble(center.get(Key.LONGITUDE.getKey()), 0.0));
    }

    public long getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(long latestTime) {
        this.latestTime = latestTime;
    }

    public Dust getDust() {
        return dust;
    }

    public void setDust(Dust dust) {
        this.dust = dust;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public enum Key {
        LATEST_TIME("latestTime"), DUST("dust"), POSITION("position"), LATITUDE("latitude"), LONGITUDE("longitude");

        private String key;

        Key(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
