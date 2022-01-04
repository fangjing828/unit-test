package barrier;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fangjing 2022-01-04.
 */
public class AtomicBarrier {
    public static void main(String[] args) {
        AtomicLong counter = new AtomicLong(100);

        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(5000));
                   counter.decrementAndGet();
                   while (!Thread.currentThread().isInterrupted() && counter.get() > 0) { }
                    System.out.println(Thread.currentThread().getId() + "  " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            thread.start();
        }
    }
}
