package com.perfect.freshair.Model;

public enum GpsProvider {
    GPS, NETWORK, TIMEOUT, UNKNOWN;

    public static GpsProvider fromString(String gpsProvider) {
        try {
            return GpsProvider.valueOf(gpsProvider);
        } catch (IllegalArgumentException | NullPointerException e) {
            return UNKNOWN;
        }
    }
}
