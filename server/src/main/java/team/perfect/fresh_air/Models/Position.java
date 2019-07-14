package team.perfect.fresh_air.Models;

import com.google.gson.JsonObject;

import team.perfect.fresh_air.DAO.DustWithLocationDAO;

public class Position {
    private double latitude;
    private double longitude;

    public Position() {
    }

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Position(DustWithLocationDAO dustWithLocation) {
        this.latitude = dustWithLocation.getLatitude();
        this.longitude = dustWithLocation.getLongitude();
    }

    public Position(JsonObject position) throws NullPointerException, ClassCastException, IllegalStateException {
        this.latitude = position.get("latitude").getAsDouble();
        this.longitude = position.get("longitude").getAsDouble();;
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
}