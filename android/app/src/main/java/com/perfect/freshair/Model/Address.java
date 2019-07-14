package com.perfect.freshair.Model;

import com.google.gson.JsonObject;

public class Address {
    private String levelOne;
    private String levelTwo;

    public Address(String levelOne, String levelTwo) {
        this.levelOne = levelOne;
        this.levelTwo = levelTwo;
    }

    public Address(JsonObject address) throws NullPointerException, ClassCastException, IllegalStateException {
        this.levelOne = address.get("addressLevelOne").getAsString();
        this.levelTwo = address.get("addressLevelTwo").getAsString();
    }

    public String getLevelOne() {
        return levelOne;
    }

    public void setLevelOne(String levelOne) {
        this.levelOne = levelOne;
    }

    public String getLevelTwo() {
        return levelTwo;
    }

    public void setLevelTwo(String levelTwo) {
        this.levelTwo = levelTwo;
    }

    @Override
    public String toString() {
        String displayAddress = levelOne;
        if (levelTwo.length() > 0)
            displayAddress += " "+levelTwo;

        return displayAddress;
    }
}
