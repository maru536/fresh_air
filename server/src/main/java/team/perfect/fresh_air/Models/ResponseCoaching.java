package team.perfect.fresh_air.Models;

import team.perfect.fresh_air.DAO.PublicDust;
import team.perfect.fresh_air.DAO.DustWithLocationDAO;

public class ResponseCoaching extends Response {
    private String coachingMessage;
    private DustWithLocationDAO latestDust;
    private PublicDust publicDust;

    public ResponseCoaching(int code, String message, String coachingMessage, DustWithLocationDAO latestDust, PublicDust publicDust) {
        super(code, message);
        this.coachingMessage = coachingMessage;
        this.latestDust = latestDust;
        this.publicDust = publicDust;
    }

    public String getCoachingMessage() {
        return this.coachingMessage;
    }

    public void setCoachingMessage(String coachingMessage) {
        this.coachingMessage = coachingMessage;
    }

    public DustWithLocationDAO getLatestDust() {
        return this.latestDust;
    }

    public void setLatestDust(DustWithLocationDAO latestDust) {
        this.latestDust = latestDust;
    }

    public PublicDust getPublicDust() {
        return this.publicDust;
    }

    public void setPublicDust(PublicDust publicDust) {
        this.publicDust = publicDust;
    }
}