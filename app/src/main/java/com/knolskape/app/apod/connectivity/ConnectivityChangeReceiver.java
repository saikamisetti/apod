package com.knolskape.app.apod.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.knolskape.app.apod.utils.Utils;

/**
 * Created by knolly on 30/11/16.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {
  public static ConnectivityReceiverListener connectivityReceiverListener;

  @Override public void onReceive(Context context, Intent intent) {
    if (connectivityReceiverListener != null) {
      boolean isConnected = !Utils.isUserOffline(context);
      connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
    }
  }

  public interface ConnectivityReceiverListener {
    void onNetworkConnectionChanged(boolean isConnected);
  }
}
