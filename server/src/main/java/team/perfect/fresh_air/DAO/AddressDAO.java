package team.perfect.fresh_air.DAO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(PositionPK.class)
@Table(name = "address")
public class AddressDAO {
    @Id
    private float latitude = -1.0f;
    @Id
    private float longitude = -1.0f;
    @Column(columnDefinition = "varchar(255) default ''")
    private String addressLevelOne = "";
    @Column(columnDefinition = "varchar(255) default ''")
    private String addressLevelTwo = "";

    public AddressDAO() {
    }

    public AddressDAO(float latitude, float longitude, String addressLevelOne, String addressLevelTwo) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressLevelOne = addressLevelOne;
        this.addressLevelTwo = addressLevelTwo;
    }

    public String getAddressLevelOne() {
        return this.addressLevelOne;
    }

    public String getAddressLevelTwo() {
        return this.addressLevelTwo;
    }
}
