package thread.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * Created by fang_j on 2021/07/16.
 */
public class DynamicThreadPool {
    private final Object lock = new Object();
    private final List<Worker> workerList;
    private final ThreadFactory factory;
    private final Runnable runnable;

    public DynamicThreadPool(String namePrefix, boolean daemon, Runnable runnable, int size) {
        workerList = new ArrayList<>();
        factory = ArtemisThreadFactory.create(namePrefix, daemon);
        this.runnable = runnable;
        changeSize(size);
    }

    public void addThread() {
        synchronized (lock) {
            Worker worker = new Worker(runnable);
            Thread thread = factory.newThread(worker);
            worker.setThread(thread);
            thread.start();
            workerList.add(worker);
        }
    }

    public void removeThread(int index) {
        synchronized (lock) {
            if (workerList.isEmpty()) {
                throw new IllegalStateException();
            }
            Worker worker = workerList.get(index);
            worker.stop();
            workerList.remove(worker);
        }
    }

    public final void changeSize(int threadCount) {
        synchronized (lock) {
            threadCount = Math.max(0, threadCount);
            while (threadCount != workerList.size()) {
                if (threadCount > workerList.size()) {
                    addThread();
                } else {
                    removeThread(0);
                }
            }
        }
    }
}
