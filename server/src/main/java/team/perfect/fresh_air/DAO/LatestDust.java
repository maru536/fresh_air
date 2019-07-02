package team.perfect.fresh_air.DAO;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.google.gson.JsonObject;

@Entity
@IdClass(DustPK.class)
@Table(name = "dust")
public class LatestDust {
    @Id
    private String userId;
    @Id
    private long time;
    private int pm25;
    private int pm100;
    private float co2;

    public LatestDust() {
    }

    public LatestDust(String userId, long time, int pm25, int pm100) {
        this.userId = userId;
        this.time = time;
        this.pm25 = pm25;
        this.pm100 = pm100;
    }

    public LatestDust(JsonObject dust) {
        this.time = dust.get("time").getAsLong();
        this.pm25 = dust.get("pm25").getAsInt();
        this.pm100 = dust.get("pm100").getAsInt();
    }

    public int getPm25() {
        return pm25;
    }

    public int getPm100() {
        return pm100;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toString() {
        return "PM2.5: " + this.pm25 + ", PM10: " + this.pm100;
    }

    public float getCo2() {
        return this.co2;
    }

    public void setCo2(float co2) {
        this.co2 = co2;
    }

}
