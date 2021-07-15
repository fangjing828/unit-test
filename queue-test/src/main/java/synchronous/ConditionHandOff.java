package synchronous;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fang_j on 2021/07/08.
 */
public class ConditionHandOff {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();

        Condition start = lock.newCondition();
        Condition pre = start;
        char[] arr = "trip".toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (i == arr.length - 1) {
                new Thread(new Task(lock, pre, start, 3, arr[i])).start();
                break;
            }
            Condition next = lock.newCondition();
            new Thread(new Task(lock, pre, next, 3, arr[i])).start();
            pre = next;
        }

        try {
            lock.lock();
            start.signal();
        } finally {
            lock.unlock();
        }
    }

    static class Task implements Runnable {
        private final ReentrantLock lock;
        private final Condition pre;
        private final Condition next;
        private final int executeCount;
        private final char c;

        public Task(ReentrantLock lock, Condition pre, Condition next, int executeCount, char c) {
            this.lock = lock;
            this.pre = pre;
            this.next = next;
            this.executeCount = executeCount;
            this.c = c;
        }

        public void run() {
            for (int i = 0; i < executeCount; i++) {
                try {
                    if (pre == next) {
                        System.out.println(c);
                        continue;
                    }
                    try {
                        lock.lock();
                        pre.await();

                        System.out.println(c);

                        next.signal();
                    } finally {
                        lock.unlock();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
