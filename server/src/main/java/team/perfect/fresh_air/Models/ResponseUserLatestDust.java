package team.perfect.fresh_air.Models;

import team.perfect.fresh_air.DAO.UserLatestDust;

public class ResponseUserLatestDust extends Response {
    private UserLatestDust dust;

    public ResponseUserLatestDust(int code, String message, UserLatestDust dust) {
        super(code, message);
        this.dust = dust;
    }

    public UserLatestDust getDust() {
        return this.dust;
    }

    public void setDust(UserLatestDust dust) {
        this.dust = dust;
    }
}