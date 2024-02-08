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
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttClient implements RiverClient, MqttCallback {
  private static final Logger log = LoggerFactory.getLogger(MqttClient.class);

  private Set<String> lades = new HashSet<>();
  private MqttAsyncClient client;
  private Callback callback;

  @Override
  public void connect(String host, int port, String username, String password) {
    try {
      client = new MqttAsyncClient(String.format("tcp://%s:%d", host, port),
          UUID.randomUUID().toString(), new MemoryPersistence());
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
  public void subscribe(String l) {
    subscribe(Set.of(l));
  }

  @Override
  public void subscribe(Set<String> l) {
    if (lades.addAll(l) && isConnected()) {
      try {
        subscribeAll(l);
      } catch (Exception e) {
        callback.onError(e);
      }
    }
  }

  private void subscribeAll(Set<String> l) throws MqttException {
    int[] qos = new int[l.size()];
    String[] topicFilters = new String[l.size()];

    Arrays.fill(qos, 1);
    topicFilters = l.toArray(topicFilters);

    client.subscribe(topicFilters, qos);
  }

  @Override
  public void unsubscribe(String l) {
    unsubscribe(Set.of(l));
  }

  @Override
  public void unsubscribe(Set<String> l) {
    if (lades.removeAll(l) && isConnected())
      try {
        unsubscribeAll(l);
      } catch (Exception e) {
        callback.onError(e);
      }
  }

  private void unsubscribeAll(Set<String> l) throws MqttException {
    String[] topicFilters = new String[l.size()];
    topicFilters = l.toArray(topicFilters);

    client.unsubscribe(topicFilters);
  }

  @Override
  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  @Override
  public void connectComplete(boolean reconnect, String serverURI) {
    try {
      subscribeAll(lades);
    } catch (Exception e) {
      callback.onError(e);
    }
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    log.debug("Message arrived: " + topic);
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
