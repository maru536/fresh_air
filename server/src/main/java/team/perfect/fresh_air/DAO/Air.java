package team.perfect.fresh_air.DAO;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.google.gson.JsonObject;

import team.perfect.fresh_air.Contract.AirContract;

@Entity
@IdClass(AddressPK.class)
public class Air {
    @Id
    private String addressLevelOne = "";
    @Id
    private String addressLevelTwo = "";
    private String dateTime = "";
    private float no2 = -1.0f;
    private float co = -1.0f;
    private float o3 = -1.0f;
    private int pm100 = -1;
    private int pm25 = -1;
    private float so2 = -1.0f;

    public Air() {
    }

    public Air(String addressLevelOne, String addressLevelTwo, float no2, float co, float o3, int pm100, int pm25,
            float so2) {
        this.addressLevelOne = addressLevelOne;
        this.addressLevelTwo = addressLevelTwo;
        this.no2 = no2;
        this.co = co;
        this.o3 = o3;
        this.pm100 = pm100;
        this.pm25 = pm25;
        this.so2 = so2;
    }

    public Air(String addressLevelOne, String addressLevelTwo, String dataTime) {
        this.addressLevelOne = addressLevelOne;
        this.addressLevelTwo = addressLevelTwo;
        this.dateTime = dataTime;
    }

    public Air(String addressLevelOne, JsonObject airData) {
        this.addressLevelOne = addressLevelOne;
        this.addressLevelTwo = airData.get(AirContract.CITY_NAME).getAsString();

        try {
            this.dateTime = airData.get(AirContract.DATA_TIME).getAsString();
        } catch (NumberFormatException e) {
            this.dateTime = "";
        }

        try {
            this.no2 = airData.get(AirContract.NO2).getAsFloat();
        } catch (NumberFormatException e) {
            this.no2 = -1.0f;
        }

        try {
            this.co = airData.get(AirContract.CO).getAsFloat();
        } catch (NumberFormatException e) {
            this.co = -1.0f;
        }

        try {
            this.o3 = airData.get(AirContract.O3).getAsFloat();
        } catch (NumberFormatException e) {
            this.o3 = -1.0f;
        }

        try {
            this.pm100 = airData.get(AirContract.PM100).getAsInt();
        } catch (NumberFormatException e) {
            this.pm100 = -1;
        }

        try {
            this.pm25 = airData.get(AirContract.PM25).getAsInt();
        } catch (NumberFormatException e) {
            this.pm25 = -1;
        }

        try {
            this.so2 = airData.get(AirContract.SO2).getAsFloat();
        } catch (NumberFormatException e) {
            this.so2 = -1.0f;
        }
    }

    public Air(JsonObject airData) {
        this.addressLevelOne = airData.get(AirContract.CITY_NAME).getAsString();

        try {
            this.dateTime = airData.get(AirContract.DATA_TIME).getAsString();
        } catch (ClassCastException | IllegalStateException e) {
            this.dateTime = "";
        }

        try {
            this.no2 = airData.get(AirContract.NO2).getAsFloat();
        } catch (ClassCastException | IllegalStateException e) {
            this.no2 = -1.0f;
        }

        try {
            this.co = airData.get(AirContract.CO).getAsFloat();
        } catch (ClassCastException | IllegalStateException e) {
            this.co = -1.0f;
        }

        try {
            this.o3 = airData.get(AirContract.O3).getAsFloat();
        } catch (ClassCastException | IllegalStateException e) {
            this.o3 = -1.0f;
        }

        try {
            this.pm100 = airData.get(AirContract.PM100).getAsInt();
        } catch (ClassCastException | IllegalStateException e) {
            this.pm100 = -1;
        }

        try {
            this.pm25 = airData.get(AirContract.PM25).getAsInt();
        } catch (ClassCastException | IllegalStateException e) {
            this.pm25 = -1;
        }

        try {
            this.so2 = airData.get(AirContract.SO2).getAsFloat();
        } catch (ClassCastException | IllegalStateException e) {
            this.so2 = -1.0f;
        }
    }

    public String getAddressLevelOne() {
        return this.addressLevelOne;
    }

    public void setAddressLevelOne(String addressLevelOne) {
        this.addressLevelOne = addressLevelOne;
    }

    public String getAddressLevelTwo() {
        return this.addressLevelTwo;
    }

    public void setAddressLevelTwo(String addressLevelTwo) {
        this.addressLevelTwo = addressLevelTwo;
    }

    public float getNo2() {
        return this.no2;
    }

    public void setNo2(float no2) {
        this.no2 = no2;
    }

    public float getCo() {
        return this.co;
    }

    public void setCo(float co) {
        this.co = co;
    }

    public float getO3() {
        return this.o3;
    }

    public void setO3(float o3) {
        this.o3 = o3;
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

    public float getSo2() {
        return this.so2;
    }

    public void setSo2(float so2) {
        this.so2 = so2;
    }
}
