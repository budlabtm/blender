package org.blab.blender.connect.mqtt;

import org.blab.blender.connect.common.MessageStream;
import org.blab.blender.connect.common.MessageStreamTask;

public class MqttSourceTask extends MessageStreamTask {
  @Override
  public Class<? extends MessageStream> getStreamClass() {
    return MqttMessageStream.class;
  }

  @Override
  public String version() {
    return new MqttSourceConnector().version();
  }
}
