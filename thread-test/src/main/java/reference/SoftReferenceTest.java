package reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangjing 2022-02-25.
 */
public class SoftReferenceTest {
    public static void main(String[] args) {
        final ReferenceQueue<byte[]> rq = new ReferenceQueue<>();
        Thread thread = new Thread(() -> {
            int cnt = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    System.out.println((cnt++) + " Remove: " + rq.remove());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        List<Object> list = new ArrayList<>(10000);
        for (int i = 0; i < 10000; i++) {
            list.add(new SoftReference<>(new byte[1024 * 1024], rq));
        }
        System.out.println("List Size: " + list.size());
    }
}
