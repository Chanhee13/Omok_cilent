import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        InetSocketAddress socketAddress = new InetSocketAddress("192.168.11.26", 6000);
        Socket socket = new Socket();
        try {
            socket.connect(socketAddress);
            System.out.println("good");

            Scanner scanner = new Scanner(System.in);
            byte[] data = new byte[1024];

            while (true) {
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                if (!scanner.hasNext()) {
                    break;
                }

                String line = scanner.next();
                os.write(line.getBytes());

                int len = is.read(data);
                if (len == -1) {
                    break;
                }

                String str = new String(data, 0, len);
                System.out.println("form Server : " + str);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
