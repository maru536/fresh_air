package team.perfect.fresh_air.Contract;

public enum TestLocationContract {
    Home(37.4773835, 126.9426012), WORK(37.4781327, 126.9493137), SCHOOL(37.4500528, 126.9503144);

    private double latitude;
    private double longitude; 

    TestLocationContract(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}