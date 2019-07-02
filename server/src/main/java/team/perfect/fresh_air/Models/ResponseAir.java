package team.perfect.fresh_air.Models;

import team.perfect.fresh_air.DAO.Air;

public class ResponseAir extends Response {
    private Air airData;

    public ResponseAir(int code, String message, Air airData) {
        super(code, message);
        this.airData = airData;
    }

    public Air getAirData() {
        return this.airData;
    }

    public void setAirData(Air airData) {
        this.airData = airData;
    }
}