package cross.thread.context;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by fangjing 2022-01-04.
 * 可以完成父线程到子线程值的传递。
 */
public class ParentChildrenThreadTransferContext {
    public static final InheritableThreadLocal<Long> userId = new InheritableThreadLocal<>();

    private final StringBuilder logs = new StringBuilder();

    void execute() {
        userId.set(ThreadLocalRandom.current().nextLong());
        try {
            Thread t1 = new Thread(this::step1);
            t1.start();
            t1.join();

            Thread t2 = new Thread(this::step2);
            t2.start();
            t2.join();

            Thread t3 = new Thread(this::step3);
            t3.start();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(logs);
    }

    public void step1() {
        log("step1");
    }

    public void step2() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log("step2");
    }

    public void step3() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log("step3");
    }

    public void log(String scope) {
        logs.append(scope);
        logs.append("\ntimestamp=" + System.currentTimeMillis());
        logs.append("\nthreadId=" + Thread.currentThread().getId());
        logs.append("\nuserId=" + userId.get());
        logs.append('\n');
    }

    public static void main(String[] args) {
        new Thread(() -> new ParentChildrenThreadTransferContext().execute()).start();
        new Thread(() -> new ParentChildrenThreadTransferContext().execute()).start();
        new Thread(() -> new ParentChildrenThreadTransferContext().execute()).start();
    }
}
