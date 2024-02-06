package org.blab.blender.connect.river;

import java.nio.channels.CompletionHandler;
import java.util.List;

/**
 * Resolver for active River lades.
 */
public interface RiverLadesResolver {
  /**
   * Start resolving active lades.
   * 
   * @param handler - completion handler, called when new lades are resolved
   */
  void start(CompletionHandler<Integer, List<String>> handler);

  /**
   * Stop resolving active lades.
   */
  void stop();

  /**
   * Exclude lades from resolving.
   * 
   * @param lades - lades to exclude
   */
  void exclude(List<String> lades);
}
