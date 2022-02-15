package cross.thread.context;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by fangjing 2022-01-04.
 * 可以完成父线程到子线程值的传递。
 */
public class ParentChildrenThreadContextPropagation {
    private static final InheritableThreadLocal<Long> context = new InheritableThreadLocal<>();

    private void execute() {
        long id = ThreadLocalRandom.current().nextLong();
        context.set(id);
        Thread thread = new Thread(() -> checkContext(id));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void checkContext(long id) {
        if (id != context.get()) {
            throw new IllegalStateException("Context propagation failed.");
        }
    }

    public static void main(String[] args) {
        new Thread(() -> new ParentChildrenThreadContextPropagation().execute()).start();
        new Thread(() -> new ParentChildrenThreadContextPropagation().execute()).start();
        new Thread(() -> new ParentChildrenThreadContextPropagation().execute()).start();
    }
}
