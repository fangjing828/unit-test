package synchronous;

/**
 * Created by fang_j on 2021/07/01.
 */
public class Synchronized_Handoff {
    public static void main(String[] args) {
        Object start = new Object();
       Object pre = start;
        char[] arr = "trip".toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (i == arr.length - 1) {
                new Thread(new Task(pre, start, 3, arr[i])).start();
                break;
            }
            Object next = new Object();
            new Thread(new Task(pre, next, 3, arr[i])).start();
            pre = next;
        }

        synchronized (start) {
            start.notify();
        }
    }

    static class Task implements Runnable {
        private final Object pre;
        private final Object next;
        private final int executeCount;
        private final char c;

        public Task(Object pre, Object next, int executeCount, char c) {
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
                    synchronized (pre) {
                        pre.wait();
                    }
                    System.out.println(c);
                    synchronized (next) {
                        next.notify();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
