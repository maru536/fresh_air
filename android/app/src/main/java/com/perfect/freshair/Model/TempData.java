package com.perfect.freshair.Model;

public class TempData {
    private int x;
    private int y;

    public TempData(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public TempData() {
        this(1,1);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
