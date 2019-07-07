package team.perfect.fresh_air.DAO;

import com.google.gson.JsonObject;

import team.perfect.fresh_air.Utils.JsonUtils;

public class Dust {
    public enum Key {
        PM100, PM25;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    private int pm100;
    private int pm25;

    public Dust(int pm100, int pm25) {
        this.pm100 = pm100;
        this.pm25 = pm25;
    }

    public Dust(JsonObject dust) {
        if (dust != null) {
            this.pm100 = JsonUtils.getAsInt(dust.get(Key.PM100.toString()), -1);
            this.pm25 = JsonUtils.getAsInt(dust.get(Key.PM25.toString()), -1);
        }
    }

    public void setPm100(int pm100) {
        this.pm100 = pm100;
    }

    public int getPm100() {
        return this.pm100;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public int getPm25() {
        return this.pm25;
    }

    public JsonObject toJsonObject() {
        JsonObject dust = new JsonObject();

        if (this.pm100 >= 0)
            dust.addProperty(Key.PM100.toString(), this.pm100);
        if (this.pm25 >= 0)
            dust.addProperty(Key.PM25.toString(), this.pm25);

        return dust;
    }
}