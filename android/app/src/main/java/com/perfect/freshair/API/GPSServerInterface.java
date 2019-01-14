package com.perfect.freshair.API;

import android.util.Log;

import com.google.gson.JsonObject;
import com.perfect.freshair.Callback.ResultCallback;
import com.perfect.freshair.Model.DustWithLocation;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GPSServerInterface {
    private static final String TAG = GPSServerInterface.class.getSimpleName();
    private Retrofit mRetrofit;

    public GPSServerInterface() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(ServerProperty.GPS_SERVER_ADDR)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void postDustWithGPS(DustWithLocation _dustWithLocation, final ResultCallback _callback) {
        GPSAPI gpsApi = mRetrofit.create(GPSAPI.class);

        JsonObject requestBody = _dustWithLocation.toJsonObject();
        requestBody.addProperty("userId", "freshAir");
        requestBody.addProperty("timestamp", System.currentTimeMillis());

        Call<JsonObject> request = gpsApi.postDustWithGPS(requestBody);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject respBody = response.body();
                if (respBody != null && respBody.get("resultCode").isJsonNull() && respBody.get("resultCode").getAsInt() == 200)
                    _callback.resultCallback(true);
                else
                    _callback.resultCallback(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                _callback.resultCallback(false);
            }
        });
    }

    public void userRegist(String _userId, String _passwd, final ResultCallback _callback) {
        GPSAPI gpsApi = mRetrofit.create(GPSAPI.class);

        JsonObject userInfo = new JsonObject();
        userInfo.addProperty("userId", _userId);
        userInfo.addProperty("passwd", _passwd);

        Call<JsonObject> request = gpsApi.userRegist(userInfo);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject respBody = response.body();
                if (respBody != null && !respBody.get("resultCode").isJsonNull() && respBody.get("resultCode").getAsInt() == 200)
                    _callback.resultCallback(true);
                else
                    _callback.resultCallback(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                _callback.resultCallback(false);
            }
        });
    }

    public void attemptSignIn(String _userId, String _passwd, final ResultCallback _callback) {
        GPSAPI gpsApi = mRetrofit.create(GPSAPI.class);

        Call<JsonObject> request = gpsApi.attemptSignIn(_userId, _passwd);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject respBody = response.body();
                if (respBody != null && respBody.get("resultCode") != null && !respBody.get("resultCode").isJsonNull() && respBody.get("resultCode").getAsInt() == 200)
                    _callback.resultCallback(true);
                else
                    _callback.resultCallback(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                _callback.resultCallback(false);
            }
        });
    }
}

