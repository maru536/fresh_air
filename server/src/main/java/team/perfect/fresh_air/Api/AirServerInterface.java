package team.perfect.fresh_air.Api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private void printStartTime(String address) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("\n\n\nStart Sync Time(" +address+ ") :: " +strDate+ "\n\n\n");
    }

    private void printEndTime(String address) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("\n\n\nEnd Sync Time(" +address+ ") :: " +strDate+ "\n\n\n");
    }

    public AirServerInterface() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).addInterceptor(interceptor).build();

        this.retrofit = new Retrofit.Builder().baseUrl(ApiContract.AIR_SERVER_ADDRESS).client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public void getLevelTwoAirData(AddressLevelOneContract address, AirRepository airRepository) {
        printStartTime(address.name());
        if (address != null) {
            AirApi airApi = retrofit.create(AirApi.class);

            Call<JsonObject> request = airApi.getLevelTwoAirData(address.getServerKey());

            try {
                JsonArray airDataList = request.execute().body().get(AirContract.LIST).getAsJsonArray();

                for (JsonElement curElem : airDataList) {
                    Air curAir = new Air(address.getBixbyKey(), curElem.getAsJsonObject());
                    airRepository.delete(curAir);
                    airRepository.save(curAir);
                }
            } catch (IOException | RuntimeException e) {

            }
        }
        printEndTime(address.name());
    }

    public void getLevelOneAirData(AirItemCodeContract itemCode, AirRepository airRepository) {
        printStartTime(itemCode.name());
        AirApi airApi = retrofit.create(AirApi.class);

        Call<JsonObject> request = airApi.getLevelOneAirData(itemCode.name().toLowerCase());

        try {
            JsonObject airData = request.execute().body().get(AirContract.LIST).getAsJsonArray().get(0).getAsJsonObject();
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
        } catch (IOException | RuntimeException e) {

        }
        
        printEndTime(itemCode.name());
    }
}