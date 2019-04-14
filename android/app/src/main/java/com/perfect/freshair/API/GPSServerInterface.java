package com.perfect.freshair.API;

import android.util.Log;

import com.google.gson.JsonObject;
import com.perfect.freshair.Callback.ResponseCallback;
import com.perfect.freshair.Model.DustGPS;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GPSServerInterface {
    private static final String TAG = GPSServerInterface.class.getSimpleName();
    private static final String RESPONSE_FAIL_MESSAGE = "Response error";
    private static final String API_FAIL_MESSAGE = "API fail";
    private static final int FAIL_CODE = -1;
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

    public void postDustGPS(String _userId, DustGPS _dustGPS, final ResponseCallback _callback) {
        GPSAPI gpsApi = mRetrofit.create(GPSAPI.class);

        JsonObject requestBody = _dustGPS.toJsonObject();
        requestBody.addProperty("userId", _userId);
        requestBody.addProperty("timestamp", System.currentTimeMillis());

        Call<JsonObject> request = gpsApi.postDustWithGPS(requestBody);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> _call, Response<JsonObject> _response) {
                responseHandler(_response.body(), _callback);
            }

            @Override
            public void onFailure(Call<JsonObject> _call, Throwable _t) {
                apiFailHandler(_t, _callback);
            }
        });
    }

    public void userRegist(String _userId, String _passwd, final ResponseCallback _callback) {
        GPSAPI gpsApi = mRetrofit.create(GPSAPI.class);

        JsonObject userInfo = new JsonObject();
        userInfo.addProperty("userId", _userId);
        userInfo.addProperty("passwd", _passwd);

        Call<JsonObject> request = gpsApi.userRegist(userInfo);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> _call, Response<JsonObject> _response) {
                responseHandler(_response.body(), _callback);
            }

            @Override
            public void onFailure(Call<JsonObject> _call, Throwable _t) {
                apiFailHandler(_t, _callback);
            }
        });
    }

    public void attemptSignIn(String _userId, String _passwd, final ResponseCallback _callback) {
        GPSAPI gpsApi = mRetrofit.create(GPSAPI.class);

        Call<JsonObject> request = gpsApi.attemptSignIn(_userId, _passwd);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> _call, Response<JsonObject> _response) {
                responseHandler(_response.body(), _callback);
            }

            @Override
            public void onFailure(Call<JsonObject> _call, Throwable _t) {
                apiFailHandler(_t, _callback);
            }
        });
    }

    private void responseHandler(JsonObject _response, final ResponseCallback _callback) {
        try {
            _callback.responseCallback(_response.get("resultCode").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, RESPONSE_FAIL_MESSAGE);
            _callback.responseCallback(FAIL_CODE);
        }
    }

    private void apiFailHandler(Throwable _t, final ResponseCallback _callback) {
        Log.e(TAG, API_FAIL_MESSAGE);
        _t.printStackTrace();
        _callback.responseCallback(FAIL_CODE);
    }
}