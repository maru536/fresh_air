package team.perfect.fresh_air.Models;

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

    public Position latitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Position longitude(double longitude) {
        this.longitude = longitude;
        return this;
    }
}