package org.blab.blender.connect.river.mqtt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.blab.blender.connect.river.Event;
import org.blab.blender.connect.river.RiverClient;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptionsBuilder;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class MqttClient implements RiverClient, MqttCallback {
  private Set<String> lades = new HashSet<>();
  private MqttAsyncClient client;
  private Callback callback;

  @Override
  public void connect(String host, int port, String username, String password) {
    try {
      client = new MqttAsyncClient(String.format("tcp://%s:%d", host, port),
          UUID.randomUUID().toString());
      client.setCallback(this);
      client.connect(new MqttConnectionOptionsBuilder().automaticReconnect(true).username(username)
          .password(password.getBytes()).build());
    } catch (Exception e) {
      if (callback != null)
        callback.onError(e);
    }
  }

  @Override
  public void disconnect() {
    if (isConnected())
      try {
        client.disconnect();
      } catch (Exception e) {
        callback.onError(e);
      }
  }

  @Override
  public boolean isConnected() {
    return client != null && client.isConnected();
  }

  @Override
  public void subscribe(String lade) {
    subscribeAll(Set.of(lade));
  }

  @Override
  public void subscribeAll(Set<String> lades) {
    if (this.lades.addAll(lades) && isConnected()) {
      int[] qos = new int[lades.size()];
      Arrays.fill(qos, 1);

      try {
        client.subscribe((String[]) lades.toArray(), qos);
      } catch (Exception e) {
        callback.onError(e);
      }
    }
  }

  @Override
  public void unsubscribe(String lade) {
    unsubscribeAll(Set.of(lade));
  }

  @Override
  public void unsubscribeAll(Set<String> lades) {
    if (this.lades.removeAll(lades) && isConnected())
      try {
        client.unsubscribe((String[]) lades.toArray());
      } catch (Exception e) {
        callback.onError(e);
      }
  }

  @Override
  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  @Override
  public void connectComplete(boolean reconnect, String serverURI) {
    int[] qos = new int[lades.size()];
    Arrays.fill(qos, 1);

    try {
      client.subscribe((String[]) lades.toArray(), qos);
    } catch (Exception e) {
      callback.onError(e);
    }
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    String[] tokens = message.toString().split("\\|");

    try {
      callback.onEvent(
          new Event(topic, Double.parseDouble(tokens[0]), Long.parseUnsignedLong(tokens[1])));
    } catch (Exception e) {
      callback.onError(e);
    }
  }

  @Override
  public void mqttErrorOccurred(MqttException exception) {
    callback.onError(exception);
  }

  @Override
  public void disconnected(MqttDisconnectResponse disconnectResponse) {}

  @Override
  public void deliveryComplete(IMqttToken token) {}

  @Override
  public void authPacketArrived(int reasonCode, MqttProperties properties) {}
}
