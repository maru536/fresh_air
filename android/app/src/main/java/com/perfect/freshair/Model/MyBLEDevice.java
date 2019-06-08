package com.perfect.freshair.Model;

import android.support.annotation.Nullable;

import java.util.Arrays;

public class MyBLEDevice {

    private String name;
    private String addr;
    private byte[] uuid;

    public MyBLEDevice() {

    }

    public MyBLEDevice(String name, String addr, byte[] uuid) {
        this.name = name;
        this.addr = addr;
        if(uuid!=null) {
            this.uuid = Arrays.copyOf(uuid, uuid.length);
        }

    }

    public byte[] getUuid() {
        return uuid;
    }

    public void setUuid(byte[] data) {
        uuid = Arrays.copyOf(data, data.length);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        MyBLEDevice temp = (MyBLEDevice) obj;
        return this.addr.equals(temp.getAddr());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
