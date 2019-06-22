package team.perfect.fresh_air.Model;

import team.perfect.fresh_air.DAO.Air;
import team.perfect.fresh_air.DAO.Dust;

public class ResponseCoaching extends Response {
    private String coachingMessage;
    private Dust latestDust;
    private Air airData;


    public ResponseCoaching(int code, String message, String coachingMessage, Dust latestDust, Air airData) {
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

    public Dust getLatestDust() {
        return this.latestDust;
    }

    public void setLatestDust(Dust latestDust) {
        this.latestDust = latestDust;
    }

    public Air getAirData() {
        return this.airData;
    }

    public void setAirData(Air airData) {
        this.airData = airData;
    }    
}