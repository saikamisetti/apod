package com.knolskape.app.apod.controllers;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.knolskape.app.apod.models.DailyPicture;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by Sai on 02-Nov-16.
 */

public class NetworkController {

    private static NetworkController instance;
    public String TAG = "NETWORKCONTROLLER";
    public static final String BASE_URL = "https://api.nasa.gov/";
    private static Retrofit retrofit = null;


    private NetworkController() {

    }
    public static NetworkController getInstance() {
        if(instance == null)
            instance = new NetworkController();
        return instance;
    }

    public static Retrofit getClient() {
        if (retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            HttpUrl url = request.url().newBuilder().addQueryParameter("api_key","DEMO_KEY").build();
                            request = request.newBuilder().url(url).build();
                            okhttp3.Response response = chain.proceed(request);

                            return response;
                        }
                    }).build();
//            client.interceptors().add(new Interceptor() {
//                @Override
//                public okhttp3.Response intercept(Chain chain) throws IOException {
//                    Request request = chain.request();
//                    HttpUrl url = request.url().newBuilder().addQueryParameter("api_key","DEMO_KEY").build();
//                    request = request.newBuilder().url(url).build();
//                    okhttp3.Response response = chain.proceed(request);
//
//                    return response;
//                }
//            });
            GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(
                    new GsonBuilder()
                            .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                            .create());
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public void getApod(Callback<DailyPicture> callback) {
        ApiInterface apiService = NetworkController.getClient().create(ApiInterface.class);
        Call<DailyPicture> call = apiService.fetchAPOD();
        call.enqueue(callback);
    }

}
