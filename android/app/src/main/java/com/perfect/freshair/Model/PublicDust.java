package com.perfect.freshair.Model;

import com.google.gson.JsonObject;

public class PublicDust {
    private Address address;
    private Dust dust;

    public PublicDust(Address address, Dust dust) {
        this.address = address;
        this.dust = dust;
    }

    public PublicDust(JsonObject publicDust) {
        this.address = new Address(publicDust);
        this.dust = new Dust(publicDust);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Dust getDust() {
        return dust;
    }

    public void setDust(Dust dust) {
        this.dust = dust;
    }
}
