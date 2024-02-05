package org.blab.blender.connect.river;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

/**
 * Single message reveived from River.
 */
public record Event(String source, double value, long timestamp) {

  /**
   * Event Avro schema. All messages from Rivers represented using that schema. Schema ID:
   * "org.blab.event".
   */
  public static final Schema SCHEMA = SchemaBuilder.record("event").namespace("org.blab").fields()
      .requiredDouble("value").requiredLong("timestamp").endRecord();


  /**
   * Serializes event into record value (payload).
   * 
   * @return Binary representation
   * @throws IOException Avro serialization exception
   */
  public byte[] serialize() throws IOException {
    GenericRecord record =
        new GenericRecordBuilder(SCHEMA).set("value", value).set("timestamp", timestamp).build();
    DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(SCHEMA);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Encoder encoder = EncoderFactory.get().binaryEncoder(output, null);

    writer.write(record, encoder);
    encoder.flush();

    return output.toByteArray();
  }

  /**
   * Create event from binary representation.
   * 
   * @param value - record value (payload)
   * @return Created event
   * @throws IOException Avro serialization exception
   */
  public static Event deserialize(String source, byte[] value) throws IOException {
    DatumReader<GenericRecord> reader = new GenericDatumReader<>(SCHEMA);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    Decoder decoder = DecoderFactory.get().binaryDecoder(input, null);
    GenericRecord record = reader.read(null, decoder);

    return new Event(source, Double.parseDouble(record.get("value").toString()),
        Long.parseUnsignedLong(record.get("timestamp").toString()));
  }

  @Override
  public boolean equals(Object event) {
    if (event == null || !(event instanceof Event))
      return false;

    Event e = (Event) event;
    return this.source.equals(e.source()) && this.value == e.value()
        && this.timestamp == e.timestamp();
  }
}
