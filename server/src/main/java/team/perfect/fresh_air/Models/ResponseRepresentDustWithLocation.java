package team.perfect.fresh_air.Models;

import java.util.List;

import com.google.gson.JsonArray;

import team.perfect.fresh_air.DAO.Dust;

public class ResponseRepresentDustWithLocation extends Response {
    private JsonArray representDustWithLocationList;
    private String userId;
    private Dust avgDust;

    public ResponseRepresentDustWithLocation(int code, String message, 
            String userId, Dust avgDust, List<RepresentDustWithLocation> dustList) {
        super(code, message);
        this.userId = userId;
        this.avgDust = avgDust;
        representDustWithLocationList = new JsonArray();
        for (RepresentDustWithLocation exploreRepresent : dustList)
            this.representDustWithLocationList.add(exploreRepresent.toJsonObject());
    }

    public JsonArray getRepresentDustWithLocationList() {
        return this.representDustWithLocationList;
    }
}