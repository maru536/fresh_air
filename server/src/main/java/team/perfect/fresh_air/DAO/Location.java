package team.perfect.fresh_air.DAO;

import com.google.gson.JsonObject;

import team.perfect.fresh_air.Utils.JsonUtils;

public class Location {
    public enum Key {
        PROVIDER, ACCURACY, LATITUDE, LONGITUDE;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    private String provider;
    private float accuracy;
    private double latitude;
    private double longitude;

    public Location(String provider, float accuracy, double latitude, double longitude) {
        this.provider = provider;
        this.accuracy = accuracy;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(JsonObject location) {
        if (location != null) {
            this.provider = JsonUtils.getAsString(location.get(Key.PROVIDER.toString()), "");
            this.accuracy = JsonUtils.getAsFloat(location.get(Key.ACCURACY.toString()), -1.0f);
            this.latitude = JsonUtils.getAsDouble(location.get(Key.LATITUDE.toString()), 0.0);
            this.longitude = JsonUtils.getAsDouble(location.get(Key.LONGITUDE.toString()), 0.0);
        }
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public float getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Location provider(String provider) {
        this.provider = provider;
        return this;
    }

    public Location accuracy(float accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    public Location latitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Location longitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public JsonObject toJsonObject() {
        JsonObject location = new JsonObject();

        location.addProperty(Key.PROVIDER.toString(), this.provider);
        location.addProperty(Key.LATITUDE.toString(), this.latitude);
        location.addProperty(Key.LONGITUDE.toString(), this.longitude);

        if (this.accuracy >= 0.0f)
            location.addProperty(Key.ACCURACY.toString(), this.accuracy);

        return location;
    }
}