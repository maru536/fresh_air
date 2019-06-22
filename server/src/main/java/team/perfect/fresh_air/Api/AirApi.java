package team.perfect.fresh_air.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AirApi {
    @GET("ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?searchCondition=HOUR&ServiceKey=SN8TsXEwCLm1FmLYwU0JBH47qlI2ALwpbf0vNwVENCO8%2Bw%2Byv7qQdO34W%2BD8RDT2eeroZacDvOvBFmfpa6XixA%3D%3D&numOfRows=999&_returnType=json")
    Call<JsonObject> getAirData(
        @Query("sidoName") String city);
}