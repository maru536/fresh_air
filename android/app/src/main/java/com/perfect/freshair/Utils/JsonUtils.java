package com.perfect.freshair.Utils;

import android.location.GnssStatus;
import android.location.Location;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonUtils {
    public static JsonArray toJsonArray(GnssStatus status) {
        int numSate = status.getSatelliteCount();
        JsonArray sateArray = new JsonArray();

        for (int i = 0; i < numSate; i++) {
            JsonObject sateInfo = new JsonObject();
            sateInfo.addProperty("AzimuthDegrees", status.getAzimuthDegrees(i));
            sateInfo.addProperty("CarrierFrequencyHz", status.getCarrierFrequencyHz(i));
            sateInfo.addProperty("Cn0DbHz", status.getCn0DbHz(i));
            sateInfo.addProperty("ConstellationType", status.getConstellationType(i));
            sateInfo.addProperty("ElevationDegrees", status.getElevationDegrees(i));
            sateInfo.addProperty("Svid", status.getSvid(i));
            sateInfo.addProperty("AlmanacData", status.hasAlmanacData(i));
            sateInfo.addProperty("CarrierFrequencyHz", status.hasCarrierFrequencyHz(i));
            sateInfo.addProperty("EphemerisData", status.hasEphemerisData(i));
            sateInfo.addProperty("UsedInFix", status.usedInFix(i));
            sateArray.add(sateInfo);
        }

        return sateArray;
    }

    public static JsonObject toJsonObject(Location location) {
        JsonObject gpsInfo = new JsonObject();

        gpsInfo.addProperty("Provider", location.getProvider());
        gpsInfo.addProperty("Accuracy", location.getAccuracy());

        return gpsInfo;
    }
}
