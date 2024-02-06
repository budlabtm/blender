package org.blab.blender.connect.river;

import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;

/**
 * Source connector for River systems.
 */
public class RiverConnector extends SourceConnector
    implements CompletionHandler<Integer, List<String>> {

  private RiverConfiguration cfg;
  private RiverLadesResolver resolver;
  private List<String> lades;

  @Override
  public void completed(Integer result, List<String> resolvedLades) {
    lades = resolvedLades;
    context.requestTaskReconfiguration();
  }

  @Override
  public void failed(Throwable exc, List<String> resolvedLades) {
    throw new ConnectException(exc);
  }

  @Override
  public void start(Map<String, String> originals) {
    try {
      cfg = new RiverConfiguration(originals);
      lades = cfg.getList(RiverConfiguration.LADES);
      List<String> exclude = cfg.getList(RiverConfiguration.LADES_EXCLUDE);

      if (lades.contains("#")) {
        lades = new ArrayList<>();
        resolver = Class.forName(cfg.getString(RiverConfiguration.LADES_RESOLVER_CLASS))
            .asSubclass(RiverLadesResolver.class).getConstructor().newInstance();
        resolver.exclude(exclude);
        resolver.start(this);
      } else
        lades = lades.stream().filter(lade -> !exclude.contains(lade)).toList();
    } catch (Exception e) {
      throw new ConnectException(e);
    }
  }

  @Override
  public void stop() {
    if (resolver != null)
      resolver.stop();
  }

  @Override
  public List<Map<String, String>> taskConfigs(int tasksMax) {
    return ConnectorUtils.groupPartitions(lades, Math.min(tasksMax, lades.size())).stream()
        .map(lades -> Map.of(RiverConfiguration.LADES, String.join(",", lades),
            RiverConfiguration.CLIENT_HOST, cfg.getString(RiverConfiguration.CLIENT_HOST),
            RiverConfiguration.CLIENT_PORT, cfg.getInt(RiverConfiguration.CLIENT_PORT).toString(),
            RiverConfiguration.CLIENT_CLASS, cfg.getString(RiverConfiguration.CLIENT_CLASS),
            RiverConfiguration.BUFFER_SIZE, cfg.getInt(RiverConfiguration.BUFFER_SIZE).toString()))
        .toList();
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
