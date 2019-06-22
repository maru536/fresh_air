package team.perfect.fresh_air.Model;

import team.perfect.fresh_air.DAO.Dust;

public class ResponseDust extends Response {
    private Dust dust;

    public ResponseDust(int code, String message, Dust dust) {
        super(code, message);
        this.dust = dust;
    }

    public Dust getDust() {
        return this.dust;
    }

    public void setDust(Dust dust) {
        this.dust = dust;
    }
}