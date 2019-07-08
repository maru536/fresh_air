package team.perfect.fresh_air.Models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import team.perfect.fresh_air.DAO.DustWithLocationDAO;

public class RepresentDustWithLocation {
    List<DustWithLocationDAO> DustWithLocationList;
    Position centerPosition;
    int sumPm100 = 0;
    int sumPm25 = 0;
    long latestTime = -1L;

    public RepresentDustWithLocation() {
        DustWithLocationList = new ArrayList<>();
        centerPosition = new Position(0.0, 0.0);
    }

    public void addDustWithLocation(DustWithLocationDAO newDustWithLocation) {
        double sumLatitude = centerPosition.getLatitude()*DustWithLocationList.size() + newDustWithLocation.getLatitude();
        double sumLongitude = centerPosition.getLongitude()*DustWithLocationList.size() + newDustWithLocation.getLongitude();
        DustWithLocationList.add(newDustWithLocation);
        centerPosition = new Position(sumLatitude / DustWithLocationList.size(), sumLongitude / DustWithLocationList.size());
        latestTime = newDustWithLocation.getTime();
        sumPm100 += newDustWithLocation.getPm100();
        sumPm25 += newDustWithLocation.getPm25();
    }

    public List<DustWithLocationDAO> getDustWithLocationList() {
        return DustWithLocationList;
    }

    public Position getCenterPosition() {
        return centerPosition;
    }

    public int getAveragePm100() {
        return sumPm100 / DustWithLocationList.size();
    }

    public int getAveragePm25() {
        return sumPm25 / DustWithLocationList.size();
    }

    public JsonObject toJsonObject() {
        JsonObject centerPosition = new JsonObject();
        centerPosition.addProperty("latitude", this.centerPosition.getLatitude());
        centerPosition.addProperty("longitude", this.centerPosition.getLongitude());

        JsonObject dust = new JsonObject();
        dust.addProperty("pm100", getAveragePm100());
        dust.addProperty("pm25", getAveragePm25());

        JsonObject object = new JsonObject();
        object.addProperty("latestTime", this.latestTime);
        object.add("dust", dust);
        object.add("centerPosition", centerPosition);

        return object;
    }
}
