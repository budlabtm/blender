package org.blab.blender.connect.river;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

class Consumer extends Thread {
  private final Buffer<Integer> buffer;
  private List<Integer> consumed;

  Consumer(Buffer<Integer> buffer) {
    this.buffer = buffer;
  }

  public List<Integer> getConsumed() {
    return consumed;
  }

  @Override
  public void run() {
    try {
      consumed = buffer.get();
    } catch (InterruptedException ignored) {
    }
  }
}


class Producer extends Thread {
  private final Buffer<Integer> buffer;
  private final List<Integer> toProduce;
  private final List<Integer> produced = new ArrayList<>();

  Producer(Buffer<Integer> buffer, List<Integer> toProduce) {
    this.buffer = buffer;
    this.toProduce = toProduce;
  }

  public List<Integer> getProduced() {
    return produced;
  }

  @Override
  public void run() {
    try {
      for (Integer i : toProduce)
        produced.add(buffer.add(i));
    } catch (InterruptedException ignored) {
    }
  }
}


@SuppressWarnings({"DuplicatedCode", "BusyWait"})
public class BufferTest {
  @Test
  public void singleProducerSingleConsumerFullTest() throws InterruptedException {
    Buffer<Integer> buffer = new Buffer<>(5);
    Producer producer = new Producer(buffer, List.of(1, 2, 3, 4, 5));
    Consumer consumer = new Consumer(buffer);

    consumer.start();
    producer.start();

    producer.join();
    consumer.join();

    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, producer.getProduced().toArray());
    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, consumer.getConsumed().toArray());
  }

  @Test
  public void singleProducerSingleConsumerPartTest() throws InterruptedException {
    Buffer<Integer> buffer = new Buffer<>(6);
    Producer producer = new Producer(buffer, List.of(1, 2, 3, 4, 5));
    Consumer consumer = new Consumer(buffer);

    consumer.start();
    producer.start();
    producer.join();

    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, producer.getProduced().toArray());
    Assert.assertTrue(consumer.isAlive());
    consumer.interrupt();
  }

  @Test
  public void singleProducerMultipleConsumerFullTest() throws InterruptedException {
    Buffer<Integer> buffer = new Buffer<>(5);
    Producer producer = new Producer(buffer, List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    Consumer consumer1 = new Consumer(buffer);
    Consumer consumer2 = new Consumer(buffer);

    producer.start();
    consumer1.start();
    consumer2.start();

    producer.join();
    consumer1.join();
    consumer2.join();

    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
        producer.getProduced().toArray());
    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, consumer1.getConsumed().toArray());
    Assert.assertArrayEquals(new Integer[] {6, 7, 8, 9, 10}, consumer2.getConsumed().toArray());
  }

  @Test
  public void singleProducerMultipleConsumerPartTest() throws InterruptedException {
    Buffer<Integer> buffer = new Buffer<>(5);
    Producer producer = new Producer(buffer, List.of(1, 2, 3, 4, 5, 6));
    Consumer consumer1 = new Consumer(buffer);
    Consumer consumer2 = new Consumer(buffer);

    producer.start();
    consumer1.start();
    consumer2.start();

    producer.join();
    consumer1.join();

    Assert.assertTrue(consumer2.isAlive());
    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6}, producer.getProduced().toArray());
    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, consumer1.getConsumed().toArray());
    consumer2.interrupt();
  }

  @Test
  public void multipleProducerSingleConsumerFullTest() throws InterruptedException {
    Buffer<Integer> buffer = new Buffer<>(10);
    Producer producer1 = new Producer(buffer, List.of(1, 2, 3, 4, 5));
    Producer producer2 = new Producer(buffer, List.of(1, 2, 3, 4, 5));
    Consumer consumer = new Consumer(buffer);

    consumer.start();
    producer1.start();
    producer2.start();

    consumer.join();
    producer1.join();
    producer2.join();

    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, producer1.getProduced().toArray());
    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, producer2.getProduced().toArray());
    Assert.assertEquals(10, consumer.getConsumed().size());
  }

  @Test
  public void multipleProducerSingleConsumerPartTest() throws InterruptedException {
    Buffer<Integer> buffer = new Buffer<>(5);
    Producer producer1 = new Producer(buffer, List.of(1, 2, 3, 4, 5));
    Producer producer2 = new Producer(buffer, List.of(1, 2, 3, 4, 5, 6));
    Consumer consumer = new Consumer(buffer);

    consumer.start();
    producer1.start();
    producer2.start();

    consumer.join();
    while (producer1.isAlive() && producer2.isAlive())
      Thread.sleep(100);

    Assert.assertTrue(producer1.isAlive() ^ producer2.isAlive());
    Assert.assertEquals(5, consumer.getConsumed().size());
  }

  @Test
  public void multipleProducerMultipleConsumerFullTest() throws InterruptedException {
    Buffer<Integer> buffer = new Buffer<>(5);
    Producer producer1 = new Producer(buffer, List.of(1, 2, 3, 4, 5));
    Producer producer2 = new Producer(buffer, List.of(1, 2, 3, 4, 5));
    Consumer consumer1 = new Consumer(buffer);
    Consumer consumer2 = new Consumer(buffer);

    consumer1.start();
    consumer2.start();
    producer1.start();
    producer2.start();

    consumer1.join();
    consumer2.join();
    producer1.join();
    producer2.join();

    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, producer1.getProduced().toArray());
    Assert.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, producer2.getProduced().toArray());
    Assert.assertEquals(5, consumer1.getConsumed().size());
    Assert.assertEquals(5, consumer2.getConsumed().size());
  }
}
