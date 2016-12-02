package com.knolskape.app.apod;

import android.app.Application;
import timber.log.Timber;

/**
 * Created by knolly on 30/11/16.
 */

public class ApodApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree() {
        @Override protected String createStackElementTag(StackTraceElement element) {
          return super.createStackElementTag(element)
              + ":"
              + element.getLineNumber(); //to return line number along with the tag
        }
      });
    }
  }
}
