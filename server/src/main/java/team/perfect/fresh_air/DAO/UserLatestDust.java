package team.perfect.fresh_air.DAO;

public class UserLatestDust {
    private String userId; 
    private long time;
    private int pm100;
    private int pm25;


    public UserLatestDust(String userId, long time, int pm100, int pm25) {
        this.userId = userId;
        this.time = time;
        this.pm100 = pm100;
        this.pm25 = pm25;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}