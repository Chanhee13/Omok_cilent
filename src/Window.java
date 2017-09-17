import processing.core.PApplet;
import processing.event.MouseEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

interface OnReceived {
    void onReceive(String packet);
}

class Reader extends Thread {
    private Socket socket;
    private OnReceived onReceived;


    Reader(Socket socket) {
        this.socket = socket;
    }

    void setOnReceived(OnReceived onReceived) {
        this.onReceived = onReceived;
    }

    @Override
    public void run() {

        try {
            InputStream is = socket.getInputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int len = is.read(buf);
                if (len == -1)
                    break;

                String packet = new String(buf, 0, len);
                if (onReceived != null) {
                    onReceived.onReceive(packet);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


public class Window extends PApplet {
    private Socket socket;
    private Reader reader;
    private int[][] stones = new int[Constants.ROW][Constants.ROW];
    private OnReceived onReceived = new OnReceived() {
        @Override
        public void onReceive(String packet) {
            System.out.println(packet);
        }
    };

    @Override
    public void settings() {
        size(640, 480);

        try {
            socket = new Socket("127.0.0.1", 5000);
            reader = new Reader(socket);
            reader.setOnReceived(onReceived);
            reader.start();

        } catch (IOException e) {
            e.printStackTrace();

            System.out.println("접속에 실패하였습니다.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        PApplet.main("Window");
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        try {
            putStone(event.getX(), event.getY());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void putStone(int x, int y) throws IOException {
        OutputStream os = socket.getOutputStream();

        // PUT:{x}:{y}
        String packet = String.format("PUT:%d:%d", x, y);
        os.write(packet.getBytes());
    }


    @Override
    public void draw() {
        this.background(248, 196, 126);
        this.drawBoard();
        drawStone();
    }


    private void drawBoard() {
        for (int i = 0; i < Constants.ROW; i++) {
            int x1 = Constants.MARGIN / 2;
            int x2 = x1 + Constants.WIDTH;
            int y1 = Constants.MARGIN / 2 + i * (Constants.WIDTH / (Constants.ROW - 1));
            int y2 = y1;
            this.line(x1, y1, x2, y2);

            x1 = Constants.MARGIN / 2 + i * (Constants.WIDTH / (Constants.ROW - 1));
            x2 = x1;
            y1 = Constants.MARGIN / 2;
            y2 = y1 + Constants.WIDTH;
            this.line(x1, y1, x2, y2);
        }
    }

    private void drawStone() {
        for (int i = 0; i < stones.length; i++) {
            for (int j = 0; j < stones[i].length; j++) {
                if (stones[i][j] == 0) {
                    continue;
                }

                int color = stones[i][j] == 1 ? 255 : 0;
//                drawStone(i, j, color);
            }
        }

    }

    @Override
    public void setup() {

    }
}
