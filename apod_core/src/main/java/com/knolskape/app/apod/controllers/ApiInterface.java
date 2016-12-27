package com.knolskape.app.apod.controllers;

import com.knolskape.app.apod.models.DailyPicture;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Sai on 02-Nov-16.
 */

public interface ApiInterface {

  @GET("planetary/apod") public Observable<DailyPicture> fetchAPOD();
}
