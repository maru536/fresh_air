package team.perfect.fresh_air.Api;

import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team.perfect.fresh_air.Contract.AddressLevelOneContract;
import team.perfect.fresh_air.Contract.AirContract;
import team.perfect.fresh_air.Contract.ApiContract;
import team.perfect.fresh_air.DAO.Air;
import team.perfect.fresh_air.Repository.AirRepository;

public class AirServerInterface {
    private Retrofit retrofit;
    
    public AirServerInterface() {
        
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)    
            .addInterceptor(interceptor).build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(ApiContract.AIR_SERVER_ADDRESS)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void getAirData(AddressLevelOneContract address, AirRepository airRepository) {
        if (address != null) {
            AirApi airApi = retrofit.create(AirApi.class);

            Call<JsonObject> request = airApi.getAirData(address.getServerKey());

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
}