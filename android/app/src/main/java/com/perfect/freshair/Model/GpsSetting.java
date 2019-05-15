package com.perfect.freshair.Model;

public class GpsSetting {
    private boolean requestGps;
    private boolean requestNetwork;
    private boolean requestPassive;

    public GpsSetting(boolean requestGps, boolean requestNetwork, boolean requestPassive) {
        this.requestGps = requestGps;
        this.requestNetwork = requestNetwork;
        this.requestPassive = requestPassive;
    }

    public boolean isRequestGps() {
        return requestGps;
    }

    public boolean isRequestNetwork() {
        return requestNetwork;
    }

    public boolean isRequestPassive() {
        return requestPassive;
    }
}
