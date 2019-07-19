package team.perfect.fresh_air.DAO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.google.gson.JsonObject;

import team.perfect.fresh_air.Contract.AirContract;
import team.perfect.fresh_air.Utils.JsonUtils;

@Entity
@IdClass(AddressPK.class)
public class PublicDust {
    @Id
    private String addressLevelOne = "";
    @Id
    private String addressLevelTwo = "";
    @Column(columnDefinition = "varchar(255) default ''")
    private String dateTime = "";
    @Column(columnDefinition = "integer default -1")
    private int pm100 = -1;
    @Column(columnDefinition = "integer default -1")
    private int pm25 = -1;

    public PublicDust() {
    }

    public PublicDust(String addressLevelOne, String addressLevelTwo, int pm100, int pm25) {
        this.addressLevelOne = addressLevelOne;
        this.addressLevelTwo = addressLevelTwo;
        this.pm100 = pm100;
        this.pm25 = pm25;
    }

    public PublicDust(String addressLevelOne, String addressLevelTwo, String dataTime) {
        this.addressLevelOne = addressLevelOne;
        this.addressLevelTwo = addressLevelTwo;
        this.dateTime = dataTime;
    }

    public PublicDust(String addressLevelOne, JsonObject airData) {
        this.addressLevelOne = addressLevelOne;

        this.addressLevelTwo = JsonUtils.getAsString(airData.get(AirContract.CITY_NAME), "");
        this.dateTime = JsonUtils.getAsString(airData.get(AirContract.DATA_TIME), "");
        this.pm100 = JsonUtils.getAsInt(airData.get(AirContract.PM100), -1);
        this.pm25 = JsonUtils.getAsInt(airData.get(AirContract.PM25), -1);
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

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTime() {
        return this.dateTime;
    }
}
