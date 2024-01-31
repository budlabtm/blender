package org.blab.blender.connect.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

/** Forwards messages from part of MessageStream sources to Kafka. */
public abstract class MessageStreamTask extends SourceTask implements MessageStream.Callback {
  private Buffer<SourceRecord> buffer;
  private MessageStream stream;

  /**
   * @return MessageStream implementation class
   */
  public abstract Class<? extends MessageStream> getStreamClass();

  @Override
  public void start(Map<String, String> properties) {
    try {
      buffer =
          new Buffer<>(
              Integer.parseUnsignedInt(properties.get(MessageStreamConfiguration.BUFFER_SIZE)));
      stream = getStreamClass().getConstructor().newInstance();
      stream.setCallback(this);
      stream.connect(properties);
    } catch (Exception e) {
      throw new ConnectException(e);
    }
  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    return buffer.get();
  }

  @Override
  public void stop() {
    try {
      stream.disconnect();
    } catch (Exception e) {
      throw new ConnectException(e);
    }
  }

  @Override
  public void onConnect(String info) {}

  @Override
  public void onDisconnect(String info) {}

  @Override
  public void onMessage(String topic, double value, long timestamp) throws InterruptedException {
    try {
      buffer.add(
          new SourceRecord(
              new HashMap<>(),
              new HashMap<>(),
              topic,
              null,
              null,
              topic,
              null,
              serializePayload(value, timestamp),
              timestamp,
              new ConnectHeaders().addString("schema", "org.blab.oneshot")));
    } catch (IOException e) {
      throw new ConnectException(e);
    }
  }

  @Override
  public void onError(Exception e) {
    throw new ConnectException(e);
  }

  private byte[] serializePayload(double value, long timestamp) throws IOException {
    GenericRecord record =
        new GenericRecordBuilder(MessageStream.SCHEMA)
            .set("value", value)
            .set("timestamp", timestamp)
            .build();

    return serializeRecord(record);
  }

  private byte[] serializeRecord(GenericRecord record) throws IOException {
    DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(MessageStream.SCHEMA);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Encoder encoder = EncoderFactory.get().binaryEncoder(output, null);

    writer.write(record, encoder);
    encoder.flush();

    return output.toByteArray();
  }
}
