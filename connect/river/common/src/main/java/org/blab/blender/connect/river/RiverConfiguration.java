package org.blab.blender.connect.river;

import java.util.Map;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

/**
 * Configurations for River source connector.
 */
public class RiverConfiguration extends AbstractConfig {
  public static final String LADES = "river.lades";
  public static final String LADES_EXCLUDE = "river.lades.exclude";
  public static final String LADES_RESOLVER_CLASS = "river.lades.resolver.class";
  public static final String CLIENT_HOST = "river.client.host";
  public static final String CLIENT_PORT = "river.client.port";
  public static final String CLIENT_CLASS = "river.client.class";
  public static final String BUFFER_SIZE = "river.buffer.size";

  public static final ConfigDef DEFINITION = new ConfigDef()
      .define(LADES, Type.LIST, Importance.HIGH, "List of lades to subscribe. Use # for all.")
      .define(LADES_EXCLUDE, Type.LIST, Importance.MEDIUM, "List of lades to exclude.")
      .define(LADES_RESOLVER_CLASS, Type.STRING, Importance.HIGH,
          "RiverLadesResolver implementation class.")
      .define(CLIENT_HOST, Type.STRING, Importance.HIGH, "River host to connect.")
      .define(CLIENT_PORT, Type.INT, Importance.HIGH, "River TCP port to connect.")
      .define(CLIENT_CLASS, Type.STRING, Importance.HIGH, "RiverClient implementation class.")
      .define(BUFFER_SIZE, Type.INT, Importance.HIGH, "RiverTask buffer size.");

  public RiverConfiguration(Map<String, String> originals) {
    super(DEFINITION, originals);
  }
}
