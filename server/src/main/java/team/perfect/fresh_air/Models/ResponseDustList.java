package team.perfect.fresh_air.Models;

import com.google.gson.JsonArray;

import team.perfect.fresh_air.DAO.LatestDust;

public class ResponseDustList extends Response {
    private JsonArray dustList;

    public ResponseDustList(int code, String message, JsonArray dustList) {
        super(code, message);
        this.dustList = dustList;
    }

    public JsonArray getDustList() {
        return this.dustList;
    }
}