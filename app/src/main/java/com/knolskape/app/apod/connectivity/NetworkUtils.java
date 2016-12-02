package com.knolskape.app.apod.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import java.util.ArrayList;

/**
 * Created by knolly on 1/12/16.
 */

public class NetworkUtils {

  private static final ArrayList<ConnectionStateChangeListener>
      connectionClassStateChangeListeners = new ArrayList<ConnectionStateChangeListener>();

  /**
   * Check is device is connected to Internet.
   */
  public static boolean isConnected(Context context) {
    try {
      ConnectivityManager cm =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      return cm.getActiveNetworkInfo().isConnectedOrConnecting();
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Check if device is connected to internet and notify.
   */
  public static void checkConnectionAndNotify(Context context) {
    if (isConnected(context)) {
      notifyConnectionRestored();
    } else {
      notifyConnectionRevoked();
    }
  }

  /**
   * Get current connection quality.
   */
  public static ConnectionQuality connectionQuality() {
    return ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
  }

  public static void watchConnection(ConnectionStateChangeListener listener) {
    if (listener != null) {
      connectionClassStateChangeListeners.add(listener);
      ConnectionClassManager.getInstance().register(listener);
    }
  }

  public static void unwatchConnection(ConnectionStateChangeListener listener) {
    if (listener != null) {
      connectionClassStateChangeListeners.remove(listener);
      ConnectionClassManager.getInstance().remove(listener);
    }
  }

  private static void notifyConnectionRevoked() {
    for (ConnectionStateChangeListener changeListener : connectionClassStateChangeListeners) {
      changeListener.onConnectionRevoked();
    }
  }

  private static void notifyConnectionRestored() {
    for (ConnectionStateChangeListener changeListener : connectionClassStateChangeListeners) {
      changeListener.onConnectionRestored();
    }
  }

  public interface ConnectionStateChangeListener
      extends ConnectionClassManager.ConnectionClassStateChangeListener {
    void onConnectionRevoked();

    void onConnectionRestored();
  }
}
