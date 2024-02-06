package org.blab.blender.connect.river;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.blab.blender.connect.river.RiverClient.Callback;

/**
 * Source task for River systems.
 */
public class RiverTask extends SourceTask implements Callback {
  private Buffer<SourceRecord> buffer;
  private List<String> lades;
  private RiverClient client;

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    return buffer.get();
  }

  @Override
  public void start(Map<String, String> properties) {
    try {
      init(properties);
      connect(properties);
    } catch (Exception e) {
      throw new ConnectException(e);
    }
  }

  private void init(Map<String, String> properties) throws Exception {
    buffer = new Buffer<>(Integer.parseUnsignedInt(properties.get(RiverConfiguration.BUFFER_SIZE)));
    lades = Arrays.stream(properties.get(RiverConfiguration.LADES).split(",")).toList();
    client = Class.forName(properties.get(RiverConfiguration.CLIENT_CLASS))
        .asSubclass(RiverClient.class).getConstructor().newInstance();
  }

  private void connect(Map<String, String> properties) {
    client.subscribeAll(lades);
    client.setCallback(this);
    client.connect(properties.get(RiverConfiguration.CLIENT_HOST),
        Integer.parseUnsignedInt(properties.get(RiverConfiguration.CLIENT_PORT)));
  }

  @Override
  public void stop() {
    if (client != null && client.isConnected())
      client.disconnect();
  }

  @Override
  public String version() {
    return new RiverConnector().version();
  }

  @Override
  public void onConnected() {}

  @Override
  public void onEvent(Event event) {
    String topic = event.source().replace("/", ".").replace("_", "-");

    try {
      buffer.add(new SourceRecord(new HashMap<>(), new HashMap<>(), topic, null, null, topic, null,
          event.serialize(), event.timestamp(), event.headers()));
    } catch (Exception e) {
      throw new ConnectException(e);
    }
  }

  @Override
  public void onError(Throwable t) {
    throw new ConnectException(t);
  }
}
