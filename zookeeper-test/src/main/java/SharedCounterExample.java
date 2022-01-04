import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by fangjing 2022-01-04.
 */
public class SharedCounterExample {
        private static final int QTY = 5;
        private static final String PATH = "/examples/counter";

        public static void main(String[] args) throws IOException, Exception {
            final Random rand = new Random();
            try (TestingServer server = new TestingServer()) {
                CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
                client.start();

                SharedCount baseCount = new SharedCount(client, PATH, 0);
                baseCount.addListener(new Listener());
                baseCount.start();

                List<SharedCount> examples = Lists.newArrayList();
                ExecutorService service = Executors.newFixedThreadPool(QTY);
                for (int i = 0; i < QTY; ++i) {
                    final SharedCount count = new SharedCount(client, PATH, 0);
                    examples.add(count);
                    Callable<Void> task = () -> {
                        count.start();
                        Thread.sleep(rand.nextInt(10000));
                        System.out.println("Increment:" + count.trySetCount(count.getVersionedValue(), count.getCount() + rand.nextInt(10)));
                        return null;
                    };
                    service.submit(task);
                }

                service.shutdown();
                service.awaitTermination(10, TimeUnit.MINUTES);

                for (int i = 0; i < QTY; ++i) {
                    examples.get(i).close();
                }
                baseCount.close();
            }
        }

        static class Listener implements SharedCountListener {
            @Override
            public void countHasChanged(SharedCountReader sharedCountReader, int i) throws Exception {
                System.out.println("Counter's value is changed to " + i);
            }

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                System.out.println("State changed: " + connectionState.toString());
            }
        }
}
