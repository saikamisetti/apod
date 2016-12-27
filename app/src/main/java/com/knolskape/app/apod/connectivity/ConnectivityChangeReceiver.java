package com.knolskape.app.apod.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by knolly on 30/11/16.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {
    NetworkUtils.checkConnectionAndNotify(context);
  }
}
