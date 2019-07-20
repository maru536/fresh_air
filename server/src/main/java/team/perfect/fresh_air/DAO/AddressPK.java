package team.perfect.fresh_air.DAO;

import java.io.Serializable;

import javax.persistence.Column;

import com.google.gson.JsonObject;

public class AddressPK implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(nullable = false)
    private String addressLevelOne = "";
    @Column(nullable = false)
    private String addressLevelTwo = "";

    public AddressPK() {
    }

    public AddressPK(String addressLevelOne, String addressLevelTwo) {
        this.addressLevelOne = addressLevelOne;
        this.addressLevelTwo = addressLevelTwo;
    }

    public AddressPK(JsonObject address) {
        if (address != null) {
            if (address.get("levelOne") != null && address.get("levelOne").isJsonPrimitive()) {
                try {
                    this.addressLevelOne = address.get("levelOne").getAsString();
                } catch (ClassCastException | IllegalStateException e) {

                }
            }

            if (address.get("levelTwo") != null && address.get("levelTwo").isJsonPrimitive()) {
                try {
                    this.addressLevelTwo = address.get("levelTwo").getAsString();
                } catch (ClassCastException | IllegalStateException e) {

                }
            }
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
    
    public JsonObject toJsonObject() {
        JsonObject address = new JsonObject();

        address.addProperty("levelOne", addressLevelOne);
        address.addProperty("levelTwo", addressLevelTwo);

        return address;
    }
}