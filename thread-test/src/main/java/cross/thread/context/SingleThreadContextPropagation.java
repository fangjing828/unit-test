package cross.thread.context;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by fangjing 2022-01-04.
 */
public class SingleThreadContextPropagation {
    private static final ThreadLocal<Long> context = new ThreadLocal<>();

    private void execute() {
        long id = ThreadLocalRandom.current().nextLong();
        context.set(id);
        checkContext(id);
    }

    private void checkContext(long id) {
        if (id != context.get()) {
            throw new IllegalStateException("Context propagation failed.");
        }
    }

    public static void main(String[] args) {
        new Thread(() -> new SingleThreadContextPropagation().execute()).start();
        new Thread(() -> new SingleThreadContextPropagation().execute()).start();
        new Thread(() -> new SingleThreadContextPropagation().execute()).start();
    }
}
