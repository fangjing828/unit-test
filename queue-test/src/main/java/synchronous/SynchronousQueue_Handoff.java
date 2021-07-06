package synchronous;

import java.util.concurrent.SynchronousQueue;

/**
 * Created by fang_j on 2021/07/01.
 */
public class SynchronousQueue_Handoff {
    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue<Long> start = new SynchronousQueue<Long>();
        SynchronousQueue<Long> pre = start;
        char[] arr = "trip".toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (i == arr.length - 1) {
                new Thread(new Task(pre, start, 3, arr[i])).start();
                break;
            }
            SynchronousQueue<Long> next = new SynchronousQueue<Long>();
            new Thread(new Task(pre, next, 3, arr[i])).start();
            pre = next;
        }

        start.put(System.currentTimeMillis());
    }

    static class Task implements Runnable {
        private final SynchronousQueue<Long> pre;
        private final SynchronousQueue<Long> next;
        private final int executeCount;
        private final char c;

        public Task(SynchronousQueue<Long> pre, SynchronousQueue<Long> next, int executeCount, char c) {
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
                    pre.take();
                    System.out.println(c);
                    next.put(System.currentTimeMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
