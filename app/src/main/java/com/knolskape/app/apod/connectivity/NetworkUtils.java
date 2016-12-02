package com.knolskape.app.apod.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by knolly on 1/12/16.
 */

public class NetworkUtils {

  private static final ArrayList<WeakReference<ConnectionStateChangeListener>>
      connectionClassStateChangeListeners =
      new ArrayList<WeakReference<ConnectionStateChangeListener>>();

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
      connectionClassStateChangeListeners.add(
          new WeakReference<ConnectionStateChangeListener>(listener));
      ConnectionClassManager.getInstance().register(listener);
    }
  }

  public static void unwatchConnection(ConnectionStateChangeListener listener) {
    if (listener != null) {
      for (WeakReference<ConnectionStateChangeListener> weakListener : connectionClassStateChangeListeners) {
        if (weakListener.get().equals(listener)) {
          connectionClassStateChangeListeners.remove(weakListener);
          break;
        }
      }
      ConnectionClassManager.getInstance().remove(listener);
    }
  }

  private static void notifyConnectionRevoked() {
    for (WeakReference<ConnectionStateChangeListener> weakListener : connectionClassStateChangeListeners) {
      weakListener.get().onConnectionRevoked();
    }
  }

  private static void notifyConnectionRestored() {
    for (WeakReference<ConnectionStateChangeListener> weakListener : connectionClassStateChangeListeners) {
      weakListener.get().onConnectionRestored();
    }
  }

  public interface ConnectionStateChangeListener
      extends ConnectionClassManager.ConnectionClassStateChangeListener {
    void onConnectionRevoked();

    void onConnectionRestored();
  }
}
