package team.perfect.fresh_air.Model;

import team.perfect.fresh_air.DAO.Air;
import team.perfect.fresh_air.DAO.LatestDust;

public class ResponseCoaching extends Response {
    private String coachingMessage;
    private LatestDust latestDust;
    private Air airData;

    public ResponseCoaching(int code, String message, String coachingMessage, LatestDust latestDust, Air airData) {
        super(code, message);
        this.coachingMessage = coachingMessage;
        this.latestDust = latestDust;
        this.airData = airData;
    }

    public String getCoachingMessage() {
        return this.coachingMessage;
    }

    public void setCoachingMessage(String coachingMessage) {
        this.coachingMessage = coachingMessage;
    }

    public LatestDust getLatestDust() {
        return this.latestDust;
    }

    public void setLatestDust(LatestDust latestDust) {
        this.latestDust = latestDust;
    }

    public Air getAirData() {
        return this.airData;
    }

    public void setAirData(Air airData) {
        this.airData = airData;
    }
}