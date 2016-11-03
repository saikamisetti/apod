package com.knolskape.app.apod.controllers;

import com.knolskape.app.apod.models.DailyPicture;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Sai on 02-Nov-16.
 */

public interface ApiInterface {

    @GET("planetary/apod")
    public Call<DailyPicture> fetchAPOD();
}
