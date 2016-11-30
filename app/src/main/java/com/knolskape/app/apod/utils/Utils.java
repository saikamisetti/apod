package com.knolskape.app.apod.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by knolly on 30/11/16.
 */

public class Utils {
  public static boolean isUserOffline(Context context) {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return !(activeNetworkInfo != null && activeNetworkInfo.isConnected());
  }
}
