package synchronous;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fangjing 2022-02-28.
 */
public class BlockingQueue {
    final Object[] items;
    final ReentrantLock lock;
    /**
     * Condition for waiting takes
     */
    private final Condition notEmpty;

    /**
     * Condition for waiting puts
     */
    private final Condition notFull;

    /**
     * items index for next take, poll, peek or remove
     */
    int takeIndex;

    /**
     * items index for next put, offer, or add
     */
    int putIndex;

    /**
     * Number of elements in the queue
     */
    int count;

    public BlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.items = new Object[capacity];
        lock = new ReentrantLock();
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    public Object take() throws InterruptedException {
        ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    public void put(Object e) throws InterruptedException {
        ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length) {
                notFull.await();
            }
            enqueue(e);
        } finally {
            lock.unlock();
        }
    }

    private void enqueue(Object e) {
        final Object[] items = this.items;
        items[putIndex] = e;
        if (++putIndex == items.length) {
            putIndex = 0;
        }
        count++;
        notEmpty.signal();
    }

    private Object dequeue() {
        final Object[] items = this.items;
        Object x = items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length) {
            takeIndex = 0;
        }
        count--;

        notFull.signal();
        return x;
    }
}
