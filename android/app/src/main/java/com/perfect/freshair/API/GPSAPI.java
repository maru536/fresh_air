package com.perfect.freshair.API;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GPSAPI {
    @POST("dust")
    Call<JsonObject> postDustWithGPS(@Body JsonObject body);

    @POST("user")
    Call<JsonObject> userRegist(@Body JsonObject body);

    @GET("user")
    Call<JsonObject> attemptSignIn(@Header("userId") String userId,
                                   @Header("passwd") String passwd);
}
