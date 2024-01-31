package org.blab.blender.connect.vcas;

import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.blab.blender.connect.common.MessageStreamConfiguration;

public class VcasSourceConfiguration extends MessageStreamConfiguration {
  public static final String VCAS_HOST = "vcas.host";
  public static final String VCAS_PORT = "vcas.port";

  public static final ConfigDef DEFINITION =
      new ConfigDef()
          .define(
              VCAS_HOST,
              ConfigDef.Type.STRING,
              ConfigDef.Importance.HIGH,
              "Source VCAS broker host.")
          .define(
              VCAS_PORT,
              ConfigDef.Type.STRING,
              ConfigDef.Importance.HIGH,
              "Source VCAS broker port.");

  public VcasSourceConfiguration(Map<String, String> originals) {
    super(DEFINITION, originals);
  }
}
