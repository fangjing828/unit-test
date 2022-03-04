package reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangjing 2022-02-25.
 */
public class PhantomReferenceTest {
    public static void main(String[] args) {
        final ReferenceQueue<byte[]> rq = new ReferenceQueue<>();
        Thread thread = new Thread(() -> {
            int cnt = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    System.out.println((cnt++) + " Remove: " + rq.remove());
                } catch (Throwable e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        List<Object> list = new ArrayList<>(10000);
        for (int i = 0; i < 10000; i++) {
            list.add(new PhantomReference(new Object(), rq));
        }
    }
}
