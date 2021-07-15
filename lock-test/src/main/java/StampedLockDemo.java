import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by fang_j on 2021/07/08.
 */
public class StampedLockDemo {
    private final Map<String, String> map = new HashMap<>();
    private final StampedLock lock = new StampedLock();

    // StampedLock 的性能比 ReentrantReadWriteLock 好
    // StampedLock 是非重入锁
    public void put(String key, String value) {
        long stamp = lock.writeLock();
        try {
            map.put(key, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public String get(String key) {
        long stamp = lock.readLock();
        try {
            return map.get(key);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    // Another feature provided by StampedLock is optimistic locking.
    // Most of the time read operations don't need to wait for write operation completion and as a result of this, the full-fledged read lock isn't required.

    // 类似于 double-check 读取变量值方法，在读多写少的情况下提高性能
    public String readWithOptimisticLock(String key) {
        long stamp = lock.tryOptimisticRead();
        String value = map.get(key);

        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                return map.get(key);
            } finally {
                lock.unlock(stamp);
            }
        }
        return value;
    }
}
