package thread.pool.schedule;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fangjing 2022-02-24.
 */
public class CancelScheduleTask {
    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    private static final AtomicLong counter = new AtomicLong();

    public static void main(String[] args) {
        ScheduledFuture future = executor.scheduleWithFixedDelay(() -> {
            System.out.println(counter.getAndIncrement() + "-" + System.currentTimeMillis());
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 1, 1, TimeUnit.SECONDS);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(future.cancel(false));
    }
}
