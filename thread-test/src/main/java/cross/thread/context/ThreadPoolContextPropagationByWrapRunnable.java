package cross.thread.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by fangjing 2022-01-04.
 */
public class ThreadPoolContextPropagationByWrapRunnable {
    private static final TransmittableThreadLocal<Long> context = new TransmittableThreadLocal<>();
    private static final ExecutorService executor = new ThreadPoolExecutor(1, 1, 1000,TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    void execute()  {
        long id = ThreadLocalRandom.current().nextLong();
        context.set(id);
        try {
            executor.submit(Objects.requireNonNull(TtlRunnable.get(() -> checkContext(id)))).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    private void checkContext(long id) {
        if (id != context.get()) {
            throw new IllegalStateException("Context propagation failed.");
        }
    }
    public static void main(String[] args) {
        new Thread(() -> new ThreadPoolContextPropagationByWrapRunnable().execute()).start();
        new Thread(() -> new ThreadPoolContextPropagationByWrapRunnable().execute()).start();
        new Thread(() -> new ThreadPoolContextPropagationByWrapRunnable().execute()).start();
    }
}
