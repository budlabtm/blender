package org.blab.blender.connect.river;

import java.nio.channels.CompletionHandler;
import java.util.Set;

/**
 * Resolver for active River lades.
 */
public interface RiverLadesResolver {
  /**
   * Start resolving active lades.
   * 
   * @param handler - completion handler, called when new lades are resolved
   */
  void start(RiverConfiguration cfg, CompletionHandler<Integer, Set<String>> handler);

  /**
   * Stop resolving active lades.
   */
  void stop();
}
