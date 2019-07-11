package team.perfect.fresh_air.DAO;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.google.gson.JsonObject;

import team.perfect.fresh_air.Utils.JsonUtils;

@Entity
@IdClass(DustPK.class)
@Table(name = "DustWithLocation")
public class DustWithLocationDAO {
    @Id
    private String userId;
    @Id
    private long time;
    private int pm100;
    private int pm25;
    private int publicPm100;
    private int publicPm25;
    private String provider;
    private float accuracy;
    private double latitude;
    private double longitude;

    public DustWithLocationDAO() {

    }

    public DustWithLocationDAO(String userId, long time, int pm100, int pm25, String provider, float accuracy,
            double latitude, double longitude) {
        this.userId = userId;
        this.time = time;
        this.pm100 = pm100;
        this.pm25 = pm25;
        this.provider = provider;
        this.accuracy = accuracy;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public DustWithLocationDAO(JsonObject dustWithLocation) {
        if (dustWithLocation != null) {
            this.userId = JsonUtils.getAsString(dustWithLocation.get(Key.USER_ID.getKey()), "");
            this.time = JsonUtils.getAsLong(dustWithLocation.get(Key.TIME.getKey()), -1L);

            this.pm100 = JsonUtils.getAsInt(dustWithLocation.get(Key.PM100.getKey()), -1);
            this.pm25 = JsonUtils.getAsInt(dustWithLocation.get(Key.PM25.getKey()), -1);

            this.provider = JsonUtils.getAsString(dustWithLocation.get(Key.PROVIDER.getKey()), "");
            this.accuracy = JsonUtils.getAsFloat(dustWithLocation.get(Key.ACCURACY.getKey()), -1.0f);
            this.latitude = JsonUtils.getAsDouble(dustWithLocation.get(Key.LATITUDE.getKey()), 0.0);
            this.longitude = JsonUtils.getAsDouble(dustWithLocation.get(Key.LONGITUDE.getKey()), 0.0);
        }
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPm100() {
        return this.pm100;
    }

    public void setPm100(int pm100) {
        this.pm100 = pm100;
    }

    public int getPm25() {
        return this.pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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

    public float getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public int getPublicPm100() {
        return this.publicPm100;
    }

    public void setPublicPm100(int publicPm100) {
        this.publicPm100 = publicPm100;
    }

    public int getPublicPm25() {
        return this.publicPm25;
    }

    public void setPublicPm25(int publicPm25) {
        this.publicPm25 = publicPm25;
    }

    public void setPublicAir(Air publicAir) {
        this.publicPm100 = publicAir.getPm100();
        this.publicPm25 = publicAir.getPm25();
    }

    public JsonObject toJsonObject() {
        JsonObject dustWithLocation = new JsonObject();

        dustWithLocation.addProperty(Key.USER_ID.getKey(), this.userId);
        dustWithLocation.addProperty(Key.TIME.getKey(), this.time);
        dustWithLocation.addProperty(Key.PM100.getKey(), this.pm100);
        dustWithLocation.addProperty(Key.PM25.getKey(), this.pm25);
        dustWithLocation.addProperty(Key.PUBLIC_PM100.getKey(), this.publicPm100);
        dustWithLocation.addProperty(Key.PUBLIC_PM25.getKey(), this.publicPm25);
        dustWithLocation.addProperty(Key.PROVIDER.getKey(), this.provider);
        dustWithLocation.addProperty(Key.ACCURACY.getKey(), this.accuracy);
        dustWithLocation.addProperty(Key.LATITUDE.getKey(), this.latitude);
        dustWithLocation.addProperty(Key.LONGITUDE.getKey(), this.longitude);

        return dustWithLocation;
    }

    public enum Key {
        USER_ID("userId"), TIME("time"), DUST("dust"), LOCATION("location"), PM100("pm100"), PM25("pm25"),
        PUBLIC_PM100("public_pm100"), PUBLIC_PM25("public_pm25"), 
        PROVIDER("provider"), ACCURACY("accuracy"), LATITUDE("latitude"), LONGITUDE("longitude");

        private String key;

        Key(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}
