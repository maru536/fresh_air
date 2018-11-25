package com.perfect.freshair.API;

import android.util.Log;

import com.google.gson.JsonObject;
import com.perfect.freshair.Model.DustWithLocation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GPSServerInterface {
    private static final String TAG = GPSServerInterface.class.getSimpleName();
    private Retrofit mRetrofit;

    public GPSServerInterface() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ServerProperty.GPS_SERVER_ADDR)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void postDustWithGPS(DustWithLocation _dustWithLocation) {
        GPSAPI gpsApi = mRetrofit.create(GPSAPI.class);

        JsonObject requestBody = _dustWithLocation.toJsonObject();
        requestBody.addProperty("userId", "freshAir");
        requestBody.addProperty("timestamp", System.currentTimeMillis());

        Call<JsonObject> request = gpsApi.postDustWithGPS(requestBody);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject respBody = response.body();
                Log.i(TAG, respBody.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
