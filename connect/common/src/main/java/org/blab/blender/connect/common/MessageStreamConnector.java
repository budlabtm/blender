package org.blab.blender.connect.common;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;

/** Forwards messages from MessageStream to Kafka. */
public abstract class MessageStreamConnector extends SourceConnector {
  protected List<String> sources;
  protected MessageStreamConfiguration configuration;

  public abstract Class<? extends MessageStreamConfiguration> getConfigurationsClass();

  public abstract List<String> extractSources(Map<String, String> properties);

  /**
   * Instantiates configuration and extract list of sources.
   *
   * @param properties startup properties passed by Kafka Connect
   */
  @Override
  public void start(Map<String, String> properties) {
    try {
      configuration = getConfigurationsClass().getConstructor().newInstance();
      sources = extractSources(properties);
    } catch (Exception e) {
      throw new ConnectException(e);
    }
  }

  /**
   * Distributes sources evenly between all tasks.
   *
   * @param tasksMax maximum number of created tasks
   * @return Configuration for each task
   */
  @Override
  public List<Map<String, String>> taskConfigs(int tasksMax) {
    return ConnectorUtils.groupPartitions(sources, Math.min(tasksMax, sources.size())).stream()
        .map(
            l -> {
              Map<String, String> props = configuration.originalsStrings();
              props.put(MessageStreamConfiguration.SOURCES, String.join(",", l));
              return props;
            })
        .collect(Collectors.toList());
  }

  @Override
  public void stop() {}
}
