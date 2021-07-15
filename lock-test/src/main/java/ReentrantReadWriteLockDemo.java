import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by fang_j on 2021/07/08.
 */
public class ReentrantReadWriteLockDemo {
    private final Map<String, String> map = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    // put 和 remove 是 写写互斥

    public void put(String key, String value) {
        writeLock.lock();
        try {
            map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    public String remove(String key){
        try {
            writeLock.lock();
            return map.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    // put 和 get 是 写读/读写互斥
    public String get(String key){
        try {
            readLock.lock();
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    // get 和 containsKey 可以同时操作
    public boolean containsKey(String key) {
        try {
            readLock.lock();
            return map.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }
}
