package org.blab.blender.connect.river;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class EventTest {
  @Test
  public void serializationLoopTest() throws IOException {
    Event event = new Event("source", 21, 101010101);
    Assert.assertEquals(event, Event.deserialize("source", event.serialize()));
  }

  @Test
  public void serializeSameTest() throws IOException {
    Event event1 = new Event("event", 0, 0);
    Event event2 = new Event("event", 0, 0);

    Assert.assertArrayEquals(event1.serialize(), event2.serialize());
  }

  @Test
  public void serializeDifferentTest() throws IOException {
    Event event1 = new Event("event1", 1, 1);
    Event event2 = new Event("event2", 2, 2);
    Assert.assertNotEquals(event1.serialize(), event2.serialize());
  }
}
