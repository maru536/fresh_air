package team.perfect.fresh_air.Models;

public class Gps {
    private String provider;
    private double lat;
    private double lng; 
    private float acc = -1.0f;

    public Gps(String provider, double lat, double lng, float acc) {
        this.provider = provider;
        this.lat = lat;
        this.lng = lng;
        this.acc = acc;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return this.lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public float getAcc() {
        return this.acc;
    }

    public void setAcc(float acc) {
        this.acc = acc;
    }
}
