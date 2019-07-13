package com.perfect.freshair.API;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DustApi {
    @POST("1.0/dust")
    Call<JsonObject> postDust(@Header("userId") String userId,
                                @Body JsonObject body);

    @POST("1.0/signUp")
    Call<JsonObject> signUp(@Body JsonObject body);

    @GET("1.0/signIn")
    Call<JsonObject> signIn(@Header("userId") String userId,
                            @Header("passwd") String passwd);


    @POST("1.0/publicDust")
    Call<JsonObject> getPublicDust(@Body JsonObject address);

    @GET("1.0/syncUserDust")
    Call<JsonObject> syncUserDust(@Header("userId") String userId);

    @GET("1.0/todayDustMap")
    Call<JsonObject> todayDustMap(@Header("userId") String userId);

    @GET("1.0/yesterdayDustMap")
    Call<JsonObject> yesterdayDustMap(@Header("userId") String userId);
}
