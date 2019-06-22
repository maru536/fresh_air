package team.perfect.fresh_air.Model;

import team.perfect.fresh_air.DAO.Air;

public class ResponseAir extends Response {
    private int pm100;
    private int pm25;

    public ResponseAir(int code, String message, int pm100, int pm25) {
        super(code, message);
        this.pm100 = pm100;
        this.pm25 = pm25;
    }

    public int getPm100() {
        return this.pm100;
    }

    public void setPm100(int pm100) {
        this.pm100 = pm100;
    }

    public int getPm25() {
        return this.pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

}