package team.perfect.fresh_air.Models;

import team.perfect.fresh_air.DAO.UserDust;

public class ResponseUserDust extends Response {
    private UserDust dust;

    public ResponseUserDust(int code, String message, UserDust dust) {
        super(code, message);
        this.dust = dust;
    }

    public UserDust getDust() {
        return this.dust;
    }

    public void setDust(UserDust dust) {
        this.dust = dust;
    }
}