package synchronous;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fang_j on 2021/07/01.
 */
public class Busywaiting_Handoff {
    public static void main(String[] args) {
        char[] arr = "trip".toCharArray();
        AtomicInteger position = new AtomicInteger();
        for (int i = 0; i < arr.length; i++) {
            new Thread(new Task(3, position, i, arr.length, arr[i])).start();
        }
    }

    static class Task implements Runnable {
        private int executeCount;
        private AtomicInteger position;
        private int relativePosition;
        private int interval;
        private char ch;

        public Task(int executeCount, AtomicInteger position, int expected, int interval, char ch) {
            this.executeCount = executeCount;
            this.position = position;
            this.relativePosition = expected;
            this.interval = interval;
            this.ch = ch;
        }

        public void run() {
            for (int i = 0; i < executeCount; i++) {
                while (!Thread.currentThread().isInterrupted()) {
                    if (position.get() % interval == relativePosition) {
                        System.out.println(ch);
                        position.incrementAndGet();
                        break;
                    }
                }
            }
        }
    }
}
