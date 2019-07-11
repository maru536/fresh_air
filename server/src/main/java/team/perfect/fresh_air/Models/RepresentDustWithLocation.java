package team.perfect.fresh_air.Models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import team.perfect.fresh_air.DAO.DustWithLocationDAO;

public class RepresentDustWithLocation {
    List<DustWithLocationDAO> DustWithLocationList;
    Position centerPosition;
    int sumPm100 = 0;
    int countPm100 = 0;
    int sumPm25 = 0;
    int countPm25 = 0;
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

        if (newDustWithLocation.getPm100() >= 0) {
            sumPm100 += newDustWithLocation.getPm100();
            countPm100++;
        }

        if (newDustWithLocation.getPm25() >= 0) {
            sumPm25 += newDustWithLocation.getPm25();
            countPm25++;
        }
    }

    public void addPublicDustWithLocation(DustWithLocationDAO newDustWithLocation) {
        double sumLatitude = centerPosition.getLatitude()*DustWithLocationList.size() + newDustWithLocation.getLatitude();
        double sumLongitude = centerPosition.getLongitude()*DustWithLocationList.size() + newDustWithLocation.getLongitude();
        DustWithLocationList.add(newDustWithLocation);
        centerPosition = new Position(sumLatitude / DustWithLocationList.size(), sumLongitude / DustWithLocationList.size());
        latestTime = newDustWithLocation.getTime();

        if (newDustWithLocation.getPublicPm100() >= 0) {
            sumPm100 += newDustWithLocation.getPublicPm100();
            countPm100++;
        }

        if (newDustWithLocation.getPublicPm25() >= 0) {
            sumPm25 += newDustWithLocation.getPublicPm25();
            countPm25++;
        }
    }

    public List<DustWithLocationDAO> getDustWithLocationList() {
        return DustWithLocationList;
    }

    public Position getCenterPosition() {
        return centerPosition;
    }

    public int getAveragePm100() {
        if (countPm100 > 0)
            return sumPm100 / countPm100;
        else
            return -1;
    }

    public int getAveragePm25() {
        if (countPm25 > 0)
            return sumPm25 / countPm25;
        else
            return -1;
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
