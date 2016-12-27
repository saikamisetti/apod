package com.knolskape.app.apod.controllers;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by Sai on 02-Nov-16.
 */

public class NetworkController {

  public static final String BASE_URL = "https://api.nasa.gov/";
  private static NetworkController instance;
  private static Retrofit retrofit = null;

  private NetworkController() {

  }

  public static NetworkController getInstance() {
    if (instance == null) instance = new NetworkController();
    return instance;
  }

  public static Retrofit getClient() {
    if (retrofit == null) {
      OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override public okhttp3.Response intercept(Chain chain) throws IOException {
          Request request = chain.request();
          HttpUrl url = request.url().newBuilder().addQueryParameter("api_key", "DEMO_KEY").build();
          request = request.newBuilder().url(url).build();
          okhttp3.Response response = chain.proceed(request);

          return response;
        }
      }).build();

      Moshi moshi = new Moshi.Builder().add(DailyPictureMoshiAdapterFactory.create()).build();
      MoshiConverterFactory moshiConverterFactory = MoshiConverterFactory.create(moshi);
      retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
          .client(client)
          .addConverterFactory(moshiConverterFactory)
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .build();
    }
    return retrofit;
  }
}
