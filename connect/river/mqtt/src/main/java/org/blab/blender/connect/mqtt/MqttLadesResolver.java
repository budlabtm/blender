package org.blab.blender.connect.mqtt;

import java.nio.channels.CompletionHandler;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.blab.blender.connect.river.RiverConfiguration;
import org.blab.blender.connect.river.RiverLadesResolver;
import org.blab.blender.connect.river.Event;
import org.blab.blender.connect.river.RiverClient.Callback;

public class MqttLadesResolver implements RiverLadesResolver, Callback {
  private MqttClient client = new MqttClient();
  private Set<String> lades = new HashSet<>();
  private CompletionHandler<Integer, Set<String>> handler;
  private Set<String> exclude;

  @Override
  public void start(RiverConfiguration cfg, CompletionHandler<Integer, Set<String>> handler) {
    this.handler = handler;
    exclude = cfg.getList(RiverConfiguration.LADES_EXCLUDE).stream().collect(Collectors.toSet());

    client.connect(cfg.getString(RiverConfiguration.CLIENT_HOST),
        cfg.getInt(RiverConfiguration.CLIENT_PORT),
        cfg.getString(RiverConfiguration.CLIENT_USERNAME),
        cfg.getString(RiverConfiguration.CLIENT_PASSWORD));
    client.setCallback(this);
    client.subscribe("#");
  }

  @Override
  public void stop() {
    client.disconnect();
  }

  @Override
  public void onConnected() {}

  @Override
  public void onEvent(Event event) {
    if (!exclude.contains(event.source()) && lades.add(event.source()))
      handler.completed(1, lades);
  }

  @Override
  public void onError(Throwable t) {
    handler.failed(t, lades);
  }
}
