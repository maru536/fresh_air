package team.perfect.fresh_air.Models;

import team.perfect.fresh_air.DAO.PublicDust;

public class ResponsePublicDust extends Response {
    PublicDust publicDust;

    public ResponsePublicDust(int code, String message, PublicDust publicDust) {
        super(code, message);
        this.publicDust = publicDust;
    }
}