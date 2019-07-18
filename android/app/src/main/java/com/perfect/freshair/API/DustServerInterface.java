package com.perfect.freshair.API;

import android.location.Location;
import android.util.Log;

import com.google.gson.JsonObject;
import com.perfect.freshair.Callback.ResponseCallback;
import com.perfect.freshair.Callback.ResponsePublicDustCallback;
import com.perfect.freshair.Callback.ResponseDustMapCallback;
import com.perfect.freshair.Model.Measurement;
import com.perfect.freshair.Model.PublicDust;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DustServerInterface {
    private static final String TAG = DustServerInterface.class.getSimpleName();
    private static final String RESPONSE_FAIL_MESSAGE = "Response error";
    private static final String API_FAIL_MESSAGE = "API fail";
    private static final int FAIL_CODE = -1;
    private Retrofit mRetrofit;

    public DustServerInterface() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(ServerProperty.GPS_SERVER_ADDR)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void postDust(String _userId, Measurement newMeasurement, final ResponseCallback _callback) {
        DustApi dustApi = mRetrofit.create(DustApi.class);
        JsonObject requestBody = newMeasurement.toJsonObject();
        Call<JsonObject> request = dustApi.postDust(_userId, requestBody);
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

    public void signUp(String userId, String passwd, final ResponseCallback _callback) {
        DustApi gpsApi = mRetrofit.create(DustApi.class);

        JsonObject userInfo = new JsonObject();
        userInfo.addProperty("userId", userId);
        userInfo.addProperty("passwd", passwd);

        Call<JsonObject> request = gpsApi.signUp(userInfo);
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

    public void signIn(String _userId, String _passwd, final ResponseCallback _callback) {
        DustApi gpsApi = mRetrofit.create(DustApi.class);

        Call<JsonObject> request = gpsApi.signIn(_userId, _passwd);
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

    public void publicDust(Location location, final ResponsePublicDustCallback callback) {
        DustApi gpsApi = mRetrofit.create(DustApi.class);

        JsonObject position = new JsonObject();
        position.addProperty("latitude", location.getLatitude());
        position.addProperty("longitude", location.getLongitude());

        Call<JsonObject> request = gpsApi.getPublicDust(position);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> _call, Response<JsonObject> _response) {
                JsonObject responseBody = _response.body();
                try {
                    callback.responsePublicDustCallback(responseBody.get("code").getAsInt(), new PublicDust(responseBody.get("publicDust").getAsJsonObject()));
                } catch (NullPointerException | ClassCastException | IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> _call, Throwable _t) {
            }
        });
    }

    public void todayDustMap(String userId, final ResponseDustMapCallback callback) {
        DustApi gpsApi = mRetrofit.create(DustApi.class);

        Call<JsonObject> request = gpsApi.todayPublicDustMap(userId);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                try {
                    callback.onResponse(body.get("code").getAsInt(), body.get("message").getAsString(), body.get("representDustWithLocationList").getAsJsonArray());
                } catch (Exception e) {
                    callback.onResponse(404, "", null);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> _call, Throwable _t) {
            }
        });
    }

    public void yesterdayDustMap(String userId, final ResponseDustMapCallback callback) {
        DustApi gpsApi = mRetrofit.create(DustApi.class);

        Call<JsonObject> request = gpsApi.yesterdayPublicDustMap(userId);
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                try {
                    callback.onResponse(body.get("code").getAsInt(), body.get("message").getAsString(), body.get("representDustWithLocationList").getAsJsonArray());
                } catch (Exception e) {
                    callback.onResponse(404, "", null);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> _call, Throwable _t) {
            }
        });
    }

    private void responseHandler(JsonObject _response, final ResponseCallback _callback) {
        try {
            _callback.responseCallback(_response.get("code").getAsInt());
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