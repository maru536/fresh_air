package team.perfect.fresh_air.DAO;

import java.io.Serializable;

import javax.persistence.Column;

public class PositionPK implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(nullable = false)
    private float latitude = -1.0f;
    @Column(nullable = false)
    private float longitude = -1.0f;

    public PositionPK() {
    }

    public PositionPK(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return this.latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return this.longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}