package com.perfect.freshair.Model;

import android.support.annotation.Nullable;

public class MyBLEDevice {

    private String name;
    private String addr;

    public MyBLEDevice() {
    }

    public MyBLEDevice(String name, String addr) {
        this.name = name;
        this.addr = addr;
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
