package team.perfect.fresh_air.Models;

import com.google.gson.JsonArray;

public class ResponseDustList extends Response {
    private JsonArray pm100List;
    private JsonArray pm25List;

    public ResponseDustList(int code, String message, JsonArray pm100List, JsonArray pm25List) {
        super(code, message);
        this.pm100List = pm100List;
        this.pm25List = pm25List;
    }
}