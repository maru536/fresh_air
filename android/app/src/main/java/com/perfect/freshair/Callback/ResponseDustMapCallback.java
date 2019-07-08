package com.perfect.freshair.Callback;

import com.google.gson.JsonArray;

public interface ResponseDustMapCallback {
    void onResponse(int code, String message, JsonArray representDustWithLocationList);
}
