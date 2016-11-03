package com.knolskape.app.apod.models;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;


/**
 * Created by Sai on 02-Nov-16.
 *
 */


@AutoValue public abstract class DailyPicture {

    @Nullable public abstract String copyright();
    @Nullable public abstract String date();
    @Nullable public abstract String explanation();
    @Nullable public abstract String hdurl();
    @Nullable public abstract String mediaType();
    @Nullable public abstract String serviceVersion();
    @Nullable public abstract String title();
    @Nullable public abstract String url();


   public static TypeAdapter<DailyPicture> typeAdapter(Gson gson) {
       return new AutoValue_DailyPicture.GsonTypeAdapter(gson);
       }

}