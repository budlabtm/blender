package org.blab.blender.connect.vcas;

import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.blab.blender.connect.common.MessageStreamConfiguration;
import org.blab.blender.connect.common.MessageStreamConnector;

public class VcasSourceConnector extends MessageStreamConnector {
  @Override
  public Class<? extends Task> taskClass() {
    return VcasSourceTask.class;
  }

  @Override
  public ConfigDef config() {
    return VcasSourceConfiguration.DEFINITION;
  }

  @Override
  public String version() {
    return "1.0";
  }

  @Override
  public Class<? extends MessageStreamConfiguration> getConfigurationsClass() {
    return VcasSourceConfiguration.class;
  }

  @Override
  public List<String> extractSources(Map<String, String> properties) {
    return configuration.getList(VcasSourceConfiguration.SOURCES);
  }
}
