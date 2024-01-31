package org.blab.blender.connect.mqtt;

import java.util.*;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.blab.blender.connect.common.MessageStreamConfiguration;
import org.blab.blender.connect.common.MessageStreamConnector;

public class MqttSourceConnector extends MessageStreamConnector {
  // TODO Add support for on-fly topic discovery.

  @Override
  public Class<? extends MessageStreamConfiguration> getConfigurationsClass() {
    return MqttSourceConfiguration.class;
  }

  @Override
  public List<String> extractSources(Map<String, String> properties) {
    List<String> sources = configuration.getList(MqttSourceConfiguration.SOURCES);
    return sources.get(0).equals("#") ? new ArrayList<>() : sources;
  }

  @Override
  public Class<? extends Task> taskClass() {
    return MqttSourceTask.class;
  }

  @Override
  public ConfigDef config() {
    return MqttSourceConfiguration.DEFINITION;
  }

  @Override
  public String version() {
    return "1.0";
  }
}
