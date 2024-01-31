package org.blab.blender.connect.vcas;

import org.blab.blender.connect.common.MessageStream;
import org.blab.blender.connect.common.MessageStreamTask;

public class VcasSourceTask extends MessageStreamTask {
  @Override
  public Class<? extends MessageStream> getStreamClass() {
    return VcasMessageStream.class;
  }

  @Override
  public String version() {
    return new VcasSourceConnector().version();
  }
}
