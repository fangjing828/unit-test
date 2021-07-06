import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fang_j on 2021/07/06.
 */
public class BIOEchoService {
    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        try(ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress("localhost", 8080));
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                executorService.submit(new IOHandler(socket));
            }
        }
    };

    static class IOHandler implements Runnable {
        private final Socket socket;

        public IOHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    InputStream is = socket.getInputStream();
                    int a;
                    while ((a = is.read()) != -1) {
                        socket.getOutputStream().write(a);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
