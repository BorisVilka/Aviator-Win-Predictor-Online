package winpredictor.game.onlinepred;

import retrofit2.Call;
import retrofit2.http.GET;

public interface API {

    @GET("/PPkdwwXZ")
    Call<Answer> getAns();


    @GET("/PPkdwwXZ")
    Call<Answer> getUrl();
}
