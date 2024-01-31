package org.blab.blender.connect.common;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe collection for streaming batches of data.
 *
 * @param <T> the type of element if buffer.
 */
public class Buffer<T> {
  private final int capacity;
  private final ArrayList<T> buffer;
  private final ReentrantLock locker;
  private final Condition condition;

  public Buffer(int capacity) {
    this.capacity = capacity;

    buffer = new ArrayList<>(capacity);
    locker = new ReentrantLock(true);
    condition = locker.newCondition();
  }

  /**
   * Pushes the element into buffer. If buffer is full, waits for consumer.
   *
   * @param element the element to push.
   */
  public T add(T element) throws InterruptedException {
    locker.lock();

    try {
      while (buffer.size() == capacity) condition.await();

      buffer.add(element);
      condition.signalAll();

      return element;
    } finally {
      locker.unlock();
    }
  }

  /**
   * Retrieves batch of records. If buffer isn't full, waits for producers.
   *
   * @return Batch of records.
   */
  @SuppressWarnings("unchecked")
  public ArrayList<T> get() throws InterruptedException {
    locker.lock();

    try {
      while (buffer.size() < capacity) condition.await();

      ArrayList<T> clone = (ArrayList<T>) buffer.clone();
      buffer.clear();
      condition.signalAll();

      return clone;
    } finally {
      locker.unlock();
    }
  }
}
