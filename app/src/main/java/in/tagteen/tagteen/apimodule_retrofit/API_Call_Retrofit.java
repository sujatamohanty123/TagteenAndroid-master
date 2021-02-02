package in.tagteen.tagteen.apimodule_retrofit;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import in.tagteen.tagteen.configurations.ServerConnector;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class API_Call_Retrofit {

  static Gson gson = new GsonBuilder()
      .setLenient()
      .create();

  public static Retrofit getretrofit(final Context ctx) {
    if (ctx != null && ctx.getClass() != null) {
      Log.e("API Call", ctx.getClass().getName());
    }
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    OkHttpClient client = builder.readTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .build();

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(ServerConnector.BASE_URL_retrofit)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build();

    return retrofit;
  }
  public static Retrofit getretrofitPost(final Context ctx) {
    if (ctx != null && ctx.getClass() != null) {
      Log.e("API Call", ctx.getClass().getName());
    }
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    OkHttpClient client = builder.readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ServerConnector.BASE_URL_retrofit_post)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build();

    return retrofit;
  }
  public static Retrofit getRetrofitWithMaxTimeout(final Context ctx) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    OkHttpClient client = builder.readTimeout(5, TimeUnit.MINUTES)
        .connectTimeout(5, TimeUnit.MINUTES)
        .build();

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(ServerConnector.BASE_URL_retrofit)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build();

    return retrofit;
  }

  public static void methodCalled(String url) {
    if (url == null) {
      return;
    }
    Log.e("API Call", url);
  }
}
