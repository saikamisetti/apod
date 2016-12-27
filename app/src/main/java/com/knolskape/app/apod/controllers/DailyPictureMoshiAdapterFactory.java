package com.knolskape.app.apod.controllers;

import com.ryanharter.auto.value.moshi.MoshiAdapterFactory;
import com.squareup.moshi.JsonAdapter;

/**
 * Created by knolly on 1/12/16.
 */

@MoshiAdapterFactory public abstract class DailyPictureMoshiAdapterFactory
    implements JsonAdapter.Factory {
  public static JsonAdapter.Factory create() {
    return new AutoValueMoshi_DailyPictureMoshiAdapterFactory();
  }
}
