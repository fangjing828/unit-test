package cross.thread.context;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by fangjing 2022-01-04.
 */
public class LocalThreadTransferContext {
    public static final ThreadLocal<Long> userId = new ThreadLocal<>();

    private final StringBuilder logs = new StringBuilder();

    void execute() {
        userId.set(ThreadLocalRandom.current().nextLong());
        step1();
        step2();
        step3();
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
        new Thread(() -> new LocalThreadTransferContext().execute()).start();
        new Thread(() -> new LocalThreadTransferContext().execute()).start();
        new Thread(() -> new LocalThreadTransferContext().execute()).start();
    }


}
