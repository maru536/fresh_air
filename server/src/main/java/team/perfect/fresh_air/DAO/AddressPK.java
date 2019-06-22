package team.perfect.fresh_air.DAO;

import java.io.Serializable;

import javax.persistence.Column;

public class AddressPK implements Serializable {
    private static final long serialVersionUID = 1L;
	@Column(nullable=false)
	private String addressLevelOne;
	@Column(nullable=false)
	private String addressLevelTwo;
	
	public AddressPK(){}

    public AddressPK(String addressLevelOne, String addressLevelTwo) {
        this.addressLevelOne = addressLevelOne;
        this.addressLevelTwo = addressLevelTwo;
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
    
}