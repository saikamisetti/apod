package com.knolskape.app.apod.models;

import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Created by Sai on 02-Nov-16.
 */

@AutoValue public abstract class DailyPicture {

  public static JsonAdapter<DailyPicture> typeAdapter(Moshi moshi) {
    return new AutoValue_DailyPicture.MoshiJsonAdapter(moshi);
  }

  @Nullable public abstract String copyright();

  @Nullable public abstract String date();

  @Nullable public abstract String explanation();

  @Nullable public abstract String hdurl();

  @Nullable public abstract String mediaType();

  @Nullable public abstract String serviceVersion();

  @Nullable public abstract String title();

  @Nullable public abstract String url();
}