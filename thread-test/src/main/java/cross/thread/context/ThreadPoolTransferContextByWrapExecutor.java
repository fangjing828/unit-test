package cross.thread.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.*;

/**
 * Created by fangjing 2022-01-04.
 */
public class ThreadPoolTransferContextByWrapExecutor {
    private static final TransmittableThreadLocal<Long> userId = new TransmittableThreadLocal<>();
    private static final ExecutorService executor = TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(1, 1, 1000,TimeUnit.SECONDS, new LinkedBlockingQueue<>()));

    private final StringBuilder logs = new StringBuilder();

    void execute()  {
        userId.set(ThreadLocalRandom.current().nextLong());
        try {
            Future future1 = executor.submit(this::step1);
            Thread.sleep(ThreadLocalRandom.current().nextLong(1000));
            Future future2 = executor.submit(this::step2);
            Thread.sleep(ThreadLocalRandom.current().nextLong(1000));
            Future future3 = executor.submit(this::step3);
            future1.get();
            future2.get();
            future3.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(logs);
    }

    public void step1() {
        log("step1");
    }

    public void step2() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log("step2");
    }

    public void step3() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log("step3");
    }

    public void log(String scope) {
        logs.append(scope);
        logs.append("\ntimestamp=" + System.currentTimeMillis());
        logs.append("\nthreadId=" + Thread.currentThread());
        logs.append("\nuserId=" + userId.get());
        logs.append('\n');
    }

    public static void main(String[] args) {
        new Thread(() -> new ThreadPoolTransferContextByWrapExecutor().execute()).start();
        new Thread(() -> new ThreadPoolTransferContextByWrapExecutor().execute()).start();
        new Thread(() -> new ThreadPoolTransferContextByWrapExecutor().execute()).start();
    }
}
