import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fang_j on 2021/07/08.
 */
public class ReentrantLockDemo {
    private final ReentrantLock lock = new ReentrantLock();
    private volatile int count;

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public void tryDoSomething() {
        boolean isLockAcquired = lock.tryLock();
        if (isLockAcquired) {
            try {
                // do something.
            } finally {
                lock.unlock();
            }
        }
    }
}
