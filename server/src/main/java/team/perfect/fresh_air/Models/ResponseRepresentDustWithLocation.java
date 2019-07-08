package team.perfect.fresh_air.Models;

import java.util.List;

import com.google.gson.JsonArray;

public class ResponseRepresentDustWithLocation extends Response {
    private JsonArray representDustWithLocationList;

    public ResponseRepresentDustWithLocation(int code, String message, List<RepresentDustWithLocation> dustList) {
        super(code, message);
        representDustWithLocationList = new JsonArray();
        for (RepresentDustWithLocation exploreRepresent : dustList)
            this.representDustWithLocationList.add(exploreRepresent.toJsonObject());
    }

    public JsonArray getRepresentDustWithLocationList() {
        return this.representDustWithLocationList;
    }
}