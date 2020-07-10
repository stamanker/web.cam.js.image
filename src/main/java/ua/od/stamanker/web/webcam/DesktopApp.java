package ua.od.stamanker.web.webcam;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.*;
import java.util.concurrent.atomic.AtomicReference;

public class DesktopApp {

    public DesktopApp process() {
        Socket socket = new Socket();
        try {
            AtomicReference<byte[]> image = WebCamManager.getInstance().image;
            if (image.get() == null) {
                System.out.println("No data");
                return this;
            }
            socket.connect(new InetSocketAddress("", 8081));
            socket.setTcpNoDelay(true);
            try (OutputStream outputStream = socket.getOutputStream()) {
                outputStream.write(image.get());
            }
            socket.close();
            System.out.println("Send");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket.isConnected()) {
                try {
                    socket.close();
                } catch (Exception ignored) {
                }
            }
        }
        return this;
    }

    public static void main(String[] args) throws InterruptedException {
        DesktopApp desktopApp = new DesktopApp();
        while (true) {
            desktopApp.process();
            Thread.sleep(2_000);
        }
    }
}
