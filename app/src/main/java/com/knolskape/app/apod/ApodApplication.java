package com.knolskape.app.apod;

import android.app.Application;
import com.knolskape.app.apod.connectivity.ConnectivityChangeReceiver;

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
  }

  public void setConnectivityListener(
      ConnectivityChangeReceiver.ConnectivityReceiverListener listener) {
    ConnectivityChangeReceiver.connectivityReceiverListener = listener;
  }
}
