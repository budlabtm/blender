package org.blab.blender.connect.vcas;

import org.blab.blender.connect.river.Event;

/**
 * Separated message parser.
 */
@FunctionalInterface
public interface MessageParser {
  Event parse(String message);
}
