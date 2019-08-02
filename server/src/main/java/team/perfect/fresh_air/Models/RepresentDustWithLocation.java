package team.perfect.fresh_air.Models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.DustWithLocationDAO;
import team.perfect.fresh_air.Repository.AddressRepository;
import team.perfect.fresh_air.Utils.ReverseGeocodingUtils;

public class RepresentDustWithLocation {
    List<DustWithLocationDAO> DustWithLocationList;
    Position position;
    int sumPm100 = 0;
    int countPm100 = 0;
    int sumPm25 = 0;
    int countPm25 = 0;
    long latestTime = -1L;

    public RepresentDustWithLocation() {
        DustWithLocationList = new ArrayList<>();
        position = new Position(0.0, 0.0);
    }

    public void addDustWithLocation(DustWithLocationDAO newDustWithLocation) {
        double sumLatitude = position.getLatitude()*DustWithLocationList.size() + newDustWithLocation.getLatitude();
        double sumLongitude = position.getLongitude()*DustWithLocationList.size() + newDustWithLocation.getLongitude();
        DustWithLocationList.add(newDustWithLocation);
        position = new Position(sumLatitude / DustWithLocationList.size(), sumLongitude / DustWithLocationList.size());
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
        double sumLatitude = position.getLatitude()*DustWithLocationList.size() + newDustWithLocation.getLatitude();
        double sumLongitude = position.getLongitude()*DustWithLocationList.size() + newDustWithLocation.getLongitude();
        DustWithLocationList.add(newDustWithLocation);
        position = new Position(sumLatitude / DustWithLocationList.size(), sumLongitude / DustWithLocationList.size());
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

    public Position getPosition() {
        return position;
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

    public JsonObject toJsonObject(AddressRepository addressRepository) {
        JsonObject position = new JsonObject();
        position.addProperty("latitude", this.position.getLatitude());
        position.addProperty("longitude", this.position.getLongitude());

        JsonObject dust = new JsonObject();
        dust.addProperty("pm100", getAveragePm100());
        dust.addProperty("pm25", getAveragePm25());

        JsonObject representDustWithLocation = new JsonObject();
        representDustWithLocation.addProperty("latestTime", this.latestTime);
        representDustWithLocation.add("dust", dust);
        representDustWithLocation.add("position", position);
        representDustWithLocation.add("address", ReverseGeocodingUtils.getAddressFromPosition(this.position, addressRepository).toJsonObject());

        return representDustWithLocation;
    }
}
