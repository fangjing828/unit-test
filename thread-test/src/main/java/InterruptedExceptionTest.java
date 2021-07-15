import java.util.concurrent.TimeUnit;

/**
 * Created by fang_j on 2021/07/07.
 */
public class InterruptedExceptionTest {
    // 如何关闭 Java 线程
    // 1. 使用 flag
    // 2. 使用线程中断
    // https://www.baeldung.com/java-thread-stop
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new CloseableTask());
        t.start();

        TimeUnit.MILLISECONDS.sleep(10);
        // 中断当前线程
        t.interrupt();
        // 等待当前线程结束
        t.join();
    }

    static class Task implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(System.currentTimeMillis());
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 将中断传递到上层，最终将任务中断掉
    static class PropagateInterruptTask implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(System.currentTimeMillis());
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // 使用额外的变量来终止线程的执行
    static class CloseableTask implements Runnable {
        private volatile boolean isClosed;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() && !isClosed) {
                System.out.println(System.currentTimeMillis());
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }

        void close() {
            isClosed = true;
        }
    }
}
