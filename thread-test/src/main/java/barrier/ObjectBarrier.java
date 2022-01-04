package barrier;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by fangjing 2022-01-04.
 */
public class ObjectBarrier {

    public static void main(String[] args) {
        Object lock = new Object();

        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(5000));
                    synchronized (lock) {
                        lock.wait();
                    }
                    System.out.println(Thread.currentThread().getId() + "  " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            thread.start();
        }

        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
