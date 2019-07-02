package team.perfect.fresh_air.Models;

import team.perfect.fresh_air.DAO.LatestDust;

public class ResponseLatestDust extends Response {
    private LatestDust dust;

    public ResponseLatestDust(int code, String message, LatestDust dust) {
        super(code, message);
        this.dust = dust;
    }

    public LatestDust getDust() {
        return this.dust;
    }

    public void setDust(LatestDust dust) {
        this.dust = dust;
    }
}