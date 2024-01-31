package org.blab.blender.connect.mqtt;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.connect.errors.ConnectException;
import org.blab.blender.connect.common.MessageStream;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

/** MQTT MessageStream implementation. */
public class MqttMessageStream implements MessageStream, MqttCallback {
  private MqttAsyncClient client;
  private MessageStream.Callback callback;
  private String[] sources;
  private int qos = 1;

  @Override
  public void connect(Map<String, String> properties) throws MqttException {
    sources = properties.get(MqttSourceConfiguration.SOURCES).split(",");
    qos = Integer.parseUnsignedInt(properties.get(MqttSourceConfiguration.MQTT_QOS));
    client =
        new MqttAsyncClient(
            properties.get(MqttSourceConfiguration.MQTT_BROKER), UUID.randomUUID().toString());
    client.connect(
        new MqttConnectionOptionsBuilder()
            .automaticReconnect(true)
            .username(properties.get(MqttSourceConfiguration.MQTT_USERNAME))
            .password(properties.get(MqttSourceConfiguration.MQTT_PASSWORD).getBytes())
            .build());
  }

  @Override
  public void disconnect() throws Exception {
    if (client != null && client.isConnected()) client.disconnect();
  }

  @Override
  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  @Override
  public void messageArrived(String topic, MqttMessage msg) throws Exception {
    String[] tokens = msg.toString().split("\\|");
    callback.onMessage(topic, Double.parseDouble(tokens[0]), Long.parseUnsignedLong(tokens[1]));
  }

  @Override
  public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
    callback.onDisconnect(mqttDisconnectResponse.getReasonString());
  }

  @Override
  public void mqttErrorOccurred(MqttException e) {
    callback.onError(e);
  }

  @Override
  public void connectComplete(boolean status, String info) {
    int[] q = new int[sources.length];

    try {
      Arrays.fill(q, qos);
      client.subscribe(sources, q);
      callback.onConnect(info);
    } catch (MqttException e) {
      throw new ConnectException(e);
    }
  }

  @Override
  public void deliveryComplete(IMqttToken iMqttToken) {}

  @Override
  public void authPacketArrived(int i, MqttProperties mqttProperties) {}
}
