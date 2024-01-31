package org.blab.blender.connect.common;

import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

/** Abstract stream of one-shot messages (measurements). */
public interface MessageStream {
  Schema SCHEMA =
      SchemaBuilder.record("oneshot")
          .namespace("org.blab")
          .fields()
          .requiredDouble("value")
          .requiredLong("timestamp")
          .endRecord();

  void connect(Map<String, String> properties) throws Exception;

  void disconnect() throws Exception;

  void setCallback(Callback callback);

  interface Callback {
    void onConnect(String info);

    void onError(Exception e);

    void onDisconnect(String info);

    void onMessage(String source, double value, long timestamp) throws InterruptedException;
  }
}
