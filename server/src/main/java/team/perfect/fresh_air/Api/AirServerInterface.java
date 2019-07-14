package team.perfect.fresh_air.Api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.perfect.fresh_air.Contract.AddressLevelOneContract;
import team.perfect.fresh_air.Contract.AirContract;
import team.perfect.fresh_air.Contract.AirItemCodeContract;
import team.perfect.fresh_air.Contract.ApiContract;
import team.perfect.fresh_air.DAO.PublicDust;
import team.perfect.fresh_air.Repository.PublicDustRepository;

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

    public void getLevelTwoAirData(AddressLevelOneContract address, PublicDustRepository airRepository) {
        if (address != null) {
            AirApi airApi = retrofit.create(AirApi.class);

            Call<JsonObject> request = airApi.getLevelTwoAirData(address.getAirKoreaKey());

            try {
                JsonArray airDataList = request.execute().body().get(AirContract.LIST).getAsJsonArray();

                for (JsonElement curElem : airDataList) {
                    PublicDust curAir = new PublicDust(address.getAddressLevelOneKey(), curElem.getAsJsonObject());
                    airRepository.upsertAir(curAir.getAddressLevelOne(), curAir.getAddressLevelTwo(), curAir.getDateTime(), curAir.getPm100(), curAir.getPm25());
                }
            } catch (IOException | RuntimeException e) {

            }
        }
    }

    public void getLevelOneAirData(AirItemCodeContract itemCode, PublicDustRepository airRepository) {
        AirApi airApi = retrofit.create(AirApi.class);

        Call<JsonObject> request = airApi.getLevelOneAirData(itemCode.name().toLowerCase());

        try {
            JsonObject airData = request.execute().body().get(AirContract.LIST).getAsJsonArray().get(0).getAsJsonObject();
            String dataTime = airData.get(AirContract.DATA_TIME).getAsString();
            for (AddressLevelOneContract address : AddressLevelOneContract.values()) {
                String lowerAddress = address.name().toLowerCase();
                if (airData.get(lowerAddress) != null) {
                    int value = airData.get(lowerAddress).getAsInt();

                    if (itemCode.equals(AirItemCodeContract.PM10))
                        airRepository.upsertPM100(address.getAddressLevelOneKey(), dataTime, value);
                    else if (itemCode.equals(AirItemCodeContract.PM25))
                        airRepository.upsertPM25(address.getAddressLevelOneKey(), dataTime, value);
                }
            }
        } catch (IOException | RuntimeException e) {

        }
    }
}