package team.perfect.fresh_air.Api;

import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.perfect.fresh_air.Contract.AddressLevelOneContract;
import team.perfect.fresh_air.Contract.AirContract;
import team.perfect.fresh_air.Contract.AirItemCodeContract;
import team.perfect.fresh_air.Contract.ApiContract;
import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.Air;
import team.perfect.fresh_air.Repository.AirRepository;

public class AirServerInterface {
    private Retrofit retrofit;

    public AirServerInterface() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).addInterceptor(interceptor).build();

        this.retrofit = new Retrofit.Builder().baseUrl(ApiContract.AIR_SERVER_ADDRESS).client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public void getLevelTwoAirData(AddressLevelOneContract address, AirRepository airRepository) {
        if (address != null) {
            AirApi airApi = retrofit.create(AirApi.class);

            Call<JsonObject> request = airApi.getLevelTwoAirData(address.getServerKey());

            request.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> _call, Response<JsonObject> _response) {
                    System.out.println(_response.body().toString());
                    JsonArray airDataList = _response.body().get(AirContract.LIST).getAsJsonArray();

                    for (JsonElement curElem : airDataList) {
                        Air curAir = new Air(address.getBixbyKey(), curElem.getAsJsonObject());
                        airRepository.delete(curAir);
                        airRepository.save(curAir);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> _call, Throwable _t) {

                }
            });
        }
    }

    public void getLevelOneAirData(AirItemCodeContract itemCode, AirRepository airRepository) {
        AirApi airApi = retrofit.create(AirApi.class);

        Call<JsonObject> request = airApi.getLevelOneAirData(itemCode.name().toLowerCase());

        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // response format 확인 후 수정 필요
                JsonObject airData = response.body().get(AirContract.LIST).getAsJsonArray().get(0).getAsJsonObject();
                String dataTime = airData.get(AirContract.DATA_TIME).getAsString();
                for (AddressLevelOneContract address : AddressLevelOneContract.values()) {
                    String lowerAddress = address.name().toLowerCase();
                    if (airData.get(lowerAddress) != null) {
                        int value = airData.get(lowerAddress).getAsInt();

                        if (airRepository.existsById(new AddressPK(address.getBixbyKey(), ""))) {
                            if (itemCode.equals(AirItemCodeContract.PM10))
                                airRepository.updatePM100(address.getBixbyKey(), dataTime, value);
                            else if (itemCode.equals(AirItemCodeContract.PM25))
                                airRepository.updatePM25(address.getBixbyKey(), dataTime, value);
                        } else {
                            Air air = new Air(address.getBixbyKey(), "", dataTime);

                            if (itemCode.equals(AirItemCodeContract.PM10))
                                air.setPm100(value);
                            else if (itemCode.equals(AirItemCodeContract.PM25))
                                air.setPm25(value);

                            airRepository.save(air);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}