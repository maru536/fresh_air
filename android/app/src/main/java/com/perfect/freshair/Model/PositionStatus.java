package com.perfect.freshair.Model;

public enum PositionStatus {
    INDOOR, OUTDOOR, UNKNOWN;

    public static PositionStatus fromString(String positionStatus) {
        try {
            return PositionStatus.valueOf(positionStatus);
        } catch (IllegalArgumentException | NullPointerException e) {
            return UNKNOWN;
        }
    }
}
