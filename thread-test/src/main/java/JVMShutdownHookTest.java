import java.util.concurrent.TimeUnit;

/**
 * Created by fangjing 2021-12-28.
 */
public class JVMShutdownHookTest {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Start execute shutdown hook");
                TimeUnit.SECONDS.sleep(5);
                System.out.println("End execute shutdown hook");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }
}
