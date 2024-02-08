package org.blab.blender.connect.vcas;

import java.net.InetSocketAddress;

/**
 * Socket for TCP channels with streaming data.
 */
public interface Socket {
  void connect(InetSocketAddress address, char endOfMessage);

  void disconnect();

  boolean isConnected();

  boolean send(String message);

  void setCallback(Callback callback);

  public interface Callback {
    void onConnected();

    void onError(Throwable t);

    void onMessage(String message);
  }
}
