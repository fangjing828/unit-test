package thread.pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fang_j on 2021/07/16.
 */
public class ArtemisThreadFactory implements ThreadFactory {
    private final AtomicLong threadNumber = new AtomicLong(1);

    private final String namePrefix;

    private final boolean daemon;

    private static final ThreadGroup threadGroup = new ThreadGroup("Artemis");

    public static ThreadFactory create(String namePrefix, boolean daemon) {
        return new ArtemisThreadFactory(namePrefix, daemon);
    }

    private ArtemisThreadFactory(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(threadGroup, runnable,//
                threadGroup.getName() + "-" + namePrefix + "-" + threadNumber.getAndIncrement());
        thread.setDaemon(daemon);
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
