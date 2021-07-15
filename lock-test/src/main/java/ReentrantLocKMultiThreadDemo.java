import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fang_j on 2021/07/12.
 */
public class ReentrantLocKMultiThreadDemo {
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread t = new Thread(() -> {
            method1();
            countDownLatch.countDown();
        });
        t.start();
        countDownLatch.await();

        Thread t2 = new Thread(() -> {
            method1();
        });
        t2.setDaemon(true);
        t2.start();
        t2.join(1000);
    }

    public static void method1() {
        lock.lock();
        try {
            System.out.println("method1");
            method2();
        } finally {
            lock.unlock();
        }
    }

    public static void method2() {
        if (lock.tryLock()) {
//            try {
                System.out.println("method2");
//            } finally {
//                lock.unlock();
//            }
        }
    }
}
