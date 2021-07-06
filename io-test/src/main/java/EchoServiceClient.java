import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by fang_j on 2021/07/06.
 */
public class EchoServiceClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8080);

         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Scanner scanner = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted()) {
            String msg = scanner.nextLine();
            out.println(msg);
            System.out.println("Response:" + in.readLine());
        }
    }
}
