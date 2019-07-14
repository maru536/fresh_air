package team.perfect.fresh_air.Models;

import team.perfect.fresh_air.DAO.PublicDust;

public class ResponseAir extends Response {
    private PublicDust airData;

    public ResponseAir(int code, String message, PublicDust airData) {
        super(code, message);
        this.airData = airData;
    }

    public PublicDust getAirData() {
        return this.airData;
    }

    public void setAirData(PublicDust airData) {
        this.airData = airData;
    }
}