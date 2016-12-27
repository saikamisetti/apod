package com.knolskape.app.apod;

import android.app.Application;
import com.knolskape.app.apod.connectivity.ConnectivityChangeReceiver;
import timber.log.Timber;

/**
 * Created by knolly on 30/11/16.
 */

public class ApodApplication extends Application {
  private static ApodApplication mInstance;

  public static synchronized ApodApplication getInstance() {
    return mInstance;
  }

  @Override public void onCreate() {
    super.onCreate();

    mInstance = this;
    if(BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree() {
      @Override protected String createStackElementTag(StackTraceElement element) {
        return super.createStackElementTag(element) + ":" + element.getLineNumber(); //to return line number along with the tag
      }
    });
  }

  public void setConnectivityListener(
      ConnectivityChangeReceiver.ConnectivityReceiverListener listener) {
    ConnectivityChangeReceiver.connectivityReceiverListener = listener;
  }
}
