package winpredictor.game.onlinepred;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;
    private static API api = null;
    private static int REQUEST_TIMEOUT = 30;
    public static String BASE_URL = "aHR0cHM6Ly9hdmlhdHJ3aW5wcmVkaWN0Lm9ubGluZS8=";

    public static Retrofit getRetrofitInstance() {
        if(retrofit!=null) return retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
        return retrofit;
    }

    public static OkHttpClient getClient() {
        if(client!=null) return client;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT,TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT,TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
        return client;
    }

    public static API getApi() {
        if(api==null) api = getRetrofitInstance().create(API.class);
        return api;
    }
}
