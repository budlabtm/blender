package org.blab.blender.connect.river;

import java.util.Collection;

/**
 * Abstract River representation.
 */
public interface RiverClient {
  /**
   * Connect to River. If there are lades already presented, subscribe on them.
   */
  void connect(String host, int port);

  /**
   * Disconnect from River. All lades will be resubscribed on reconnect.
   */
  void disconnect();

  /**
   * Check wether River connected or not.
   */
  boolean isConnected();

  /**
   * Subscribe to lade. If River disconnected, subscribes after reconnect.
   * 
   * @param lade - lade to subscribe
   */
  void subscribe(String lade);

  /**
   * Subscribe to multiple lades. If River disconnected, subscribes after reconnect.
   * 
   * @param lades - lades to subscribe
   */
  void subscribeAll(Collection<String> lades);

  /**
   * Unsubscribe from lade. If River disconnected, unsubscribes after reconnect.
   * 
   * @param lade - lade to unsubscribe
   */
  void unsubscribe(String lade);

  /**
   * Unsubscribe from multiple lades. If River disconnected, unsubscribes after reconnect.
   * 
   * @param lades - lades to unsubscribe
   */
  void unsubscribeAll(Collection<String> lades);

  void setCallback(Callback callback);

  /**
   * Callback implemented by calling party.
   */
  public interface Callback {
    /**
     * Called when client successfully connected. When client disconnected by recoverable reason,
     * automatic reconnect performed.
     */
    void onConnected();

    /**
     * Called on event received.
     * 
     * @param event - received event
     */
    void onEvent(Event event);

    /**
     * Called on fatal error occured.
     * 
     * @param t - occured error
     */
    void onError(Throwable t);
  }
}
