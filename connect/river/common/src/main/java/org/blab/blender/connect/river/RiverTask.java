package org.blab.blender.connect.river;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.blab.blender.connect.river.RiverClient.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Source task for River systems.
 */
public class RiverTask extends SourceTask implements Callback {
  private static final Logger log = LoggerFactory.getLogger(RiverTask.class);

  private Buffer<SourceRecord> buffer;
  private Set<String> lades;
  private RiverClient client;

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    return buffer.get();
  }

  @Override
  public void start(Map<String, String> properties) {
    RiverConfiguration cfg = new RiverConfiguration(properties);

    buffer = new Buffer<>(cfg.getInt(RiverConfiguration.BUFFER_SIZE));
    lades = cfg.getList(RiverConfiguration.LADES).stream().collect(Collectors.toSet());

    log.debug("Staring for: " + lades);

    try {
      connect(cfg);
    } catch (Exception e) {
      throw new ConnectException(e);
    }
  }

  private void connect(RiverConfiguration cfg) throws Exception {
    client = Class.forName(cfg.getString(RiverConfiguration.CLIENT_CLASS))
        .asSubclass(RiverClient.class).getConstructor().newInstance();
    client.subscribe(lades);
    client.setCallback(this);
    client.connect(cfg.getString(RiverConfiguration.CLIENT_HOST),
        cfg.getInt(RiverConfiguration.CLIENT_PORT),
        cfg.getString(RiverConfiguration.CLIENT_USERNAME),
        cfg.getString(RiverConfiguration.CLIENT_PASSWORD));
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
