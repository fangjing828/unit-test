package thread.pool;

/**
 * Created by fang_j on 2021/07/16.
 */
public class Worker implements Runnable{
    private volatile boolean running;
    private final Runnable task;
    private Thread thread;

    public Worker(Runnable task) {
        this.task = task;
        this.running = true;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && running) {
            task.run();
        }
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }
}