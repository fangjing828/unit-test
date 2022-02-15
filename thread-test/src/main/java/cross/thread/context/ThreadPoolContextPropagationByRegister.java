package cross.thread.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by fangjing 2022-01-07.
 * 兼容现有代码
 */
public class ThreadPoolContextPropagationByRegister {
    private static final ThreadLocal<Long> context = new ThreadLocal<>();
    private static final ExecutorService executor = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    void execute()  {
        TransmittableThreadLocal.Transmitter.registerThreadLocal(context, parentValue -> parentValue);
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
        new Thread(() -> new ThreadPoolContextPropagationByRegister().execute()).start();
        new Thread(() -> new ThreadPoolContextPropagationByRegister().execute()).start();
        new Thread(() -> new ThreadPoolContextPropagationByRegister().execute()).start();
    }
}
