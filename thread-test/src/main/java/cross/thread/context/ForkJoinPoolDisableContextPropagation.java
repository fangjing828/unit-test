package cross.thread.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by fangjing 2022-01-07.
 */
public class ForkJoinPoolDisableContextPropagation {
    private static final TransmittableThreadLocal<Long> context = new TransmittableThreadLocal<>();

    public static void main(String[] args) {
        context.set(ThreadLocalRandom.current().nextLong());
        int n = 5;

        ForkJoinPool.ForkJoinWorkerThreadFactory factory = pool -> {
            final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            worker.setName("my-thread" + worker.getPoolIndex());
            return worker;
        };

        factory = TtlForkJoinPoolHelper.getDisableInheritableForkJoinWorkerThreadFactory(factory);

        //创建分治任务线程池，可以追踪到线程名称
        ForkJoinPool forkJoinPool = new ForkJoinPool(4, factory, null, false);

        // 快速创建 ForkJoinPool 方法
        // ForkJoinPool forkJoinPool = new ForkJoinPool(4);

        //创建分治任务
        Fibonacci fibonacci = new Fibonacci(n);


        //调用 invoke 方法启动分治任务
        Integer result = forkJoinPool.invoke(fibonacci);

    }

    static class Fibonacci extends RecursiveTask<Integer> {
        final int n;

        Fibonacci(int n) {
            this.n = n;
        }

        @Override
        public Integer compute() {
            System.out.println("forkJoinPool id is " + context.get());

            if (n <= 1) {
                return n;
            }

            Fibonacci f1 = new Fibonacci(n - 1);
            f1.fork();
            Fibonacci f2 = new Fibonacci(n - 2);
            return f2.compute() + f1.join();
        }
    }
}
