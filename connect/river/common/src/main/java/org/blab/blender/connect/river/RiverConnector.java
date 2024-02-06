package org.blab.blender.connect.river;

import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;

/**
 * Source connector for River systems.
 */
public class RiverConnector extends SourceConnector
    implements CompletionHandler<Integer, Set<String>> {

  private RiverConfiguration cfg;
  private RiverLadesResolver resolver;
  private Set<String> lades;
  private Set<String> exclude;

  @Override
  public void completed(Integer result, Set<String> resolvedLades) {
    lades = resolvedLades;
    context.requestTaskReconfiguration();
  }

  @Override
  public void failed(Throwable exc, Set<String> resolvedLades) {
    throw new ConnectException(exc);
  }

  @Override
  public void start(Map<String, String> originals) {
    try {
      cfg = new RiverConfiguration(originals);
      lades = cfg.getList(RiverConfiguration.LADES).stream().collect(Collectors.toSet());
      exclude = cfg.getList(RiverConfiguration.LADES_EXCLUDE).stream().collect(Collectors.toSet());

      if (lades.contains("#"))
        resolveLades();
      else
        lades.removeAll(exclude);
    } catch (Exception e) {
      throw new ConnectException(e);
    }
  }

  private void resolveLades() throws Exception {
    lades = Set.of();
    resolver = Class.forName(cfg.getString(RiverConfiguration.LADES_RESOLVER_CLASS))
        .asSubclass(RiverLadesResolver.class).getConstructor().newInstance();
    resolver.start(cfg, this);
  }

  @Override
  public void stop() {
    if (resolver != null)
      resolver.stop();
  }

  @Override
  public List<Map<String, String>> taskConfigs(int tasksMax) {
    return ConnectorUtils.groupPartitions(lades.stream().toList(), Math.min(tasksMax, lades.size()))
        .stream().map(lades -> {
          Map<String, String> properties = cfg.originalsStrings();
          properties.put(RiverConfiguration.LADES, String.join(", ", lades));
          return properties;
        }).toList();
  }

  @Override
  public Class<? extends Task> taskClass() {
    return RiverTask.class;
  }

  @Override
  public ConfigDef config() {
    return RiverConfiguration.DEFINITION;
  }

  @Override
  public String version() {
    return "1.0";
  }
}
