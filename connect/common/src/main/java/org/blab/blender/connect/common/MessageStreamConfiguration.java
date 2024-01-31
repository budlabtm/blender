package org.blab.blender.connect.common;

import java.util.Map;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

/** Common configurations for MessageStreams */
public abstract class MessageStreamConfiguration extends AbstractConfig {
  public static final String SOURCES = "sources";
  public static final String BUFFER_SIZE = "buffer.size";

  /**
   * Adds extra definitions to implementation's one and parses provided properties.
   *
   * @param definition implementation's definition
   * @param originals properties provided by Kafka Connect
   */
  public MessageStreamConfiguration(ConfigDef definition, Map<String, String> originals) {
    super(
        definition
            .define(
                SOURCES,
                ConfigDef.Type.LIST,
                ConfigDef.Importance.HIGH,
                "Sources list which must be observed.")
            .define(
                BUFFER_SIZE,
                ConfigDef.Type.INT,
                1,
                ConfigDef.Importance.MEDIUM,
                "Size of buffer between MessageStream and Kafka."),
        originals);
  }
}
