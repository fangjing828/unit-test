package barrier;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by fangjing 2022-01-04.
 * 使用 CountDownLatch 模拟线程 Barrier
 */
public class ThreadBarrier {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(5000));
                    countDownLatch.countDown();
                    countDownLatch.await();
                    System.out.println(Thread.currentThread().getId() + "  " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            thread.start();
        }
    }
}
