package org.blab.blender.connect.mqtt;

import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.blab.blender.connect.common.MessageStreamConfiguration;

public class MqttSourceConfiguration extends MessageStreamConfiguration {
  // TODO Add support for non-required fields.

  public static final String MQTT_BROKER = "mqtt.broker";
  public static final String MQTT_USERNAME = "mqtt.username";
  public static final String MQTT_PASSWORD = "mqtt.password";
  public static final String MQTT_QOS = "mqtt.qos";

  public static final ConfigDef DEFINITION =
      new ConfigDef()
          .define(MQTT_BROKER, Type.STRING, Importance.HIGH, "Source MQTT broker address.")
          .define(
              MQTT_USERNAME,
              Type.STRING,
              "",
              Importance.LOW,
              "Username to use, if authentication required.")
          .define(
              MQTT_PASSWORD,
              Type.STRING,
              "",
              Importance.LOW,
              "Password to use, if authentication required.")
          .define(
              MQTT_QOS,
              Type.INT,
              1,
              Importance.LOW,
              "Quality of Service for messages that must be observed.");

  public MqttSourceConfiguration(Map<String, String> originals) {
    super(DEFINITION, originals);
  }
}
