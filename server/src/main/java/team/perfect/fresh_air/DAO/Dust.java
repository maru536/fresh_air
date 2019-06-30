package team.perfect.fresh_air.DAO;

public class Dust {
    private int pm100;
    private int pm25;

    public Dust(int pm100, int pm25) {
        this.pm100 = pm100;
        this.pm25 = pm25;
    }

    public void setPm100(int pm100) {
        this.pm100 = pm100;
    }

    public int getPm100() {
        return this.pm100;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public int getPm25() {
        return this.pm25;
    }
}