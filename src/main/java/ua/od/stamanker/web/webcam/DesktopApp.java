package ua.od.stamanker.web.webcam;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.*;
import java.util.concurrent.atomic.AtomicReference;

public class DesktopApp {

    WebCamManager webCamManager;

    private DesktopApp init() {
        webCamManager = WebCamManager.getInstance();
        return this;
    }

    public DesktopApp process() {
        AtomicReference<byte[]> image = webCamManager.image;
        if (image.get() == null) {
            System.out.println("No data");
            return this;
        }
        Socket socket = new Socket();
        try {
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
        DesktopApp desktopApp = new DesktopApp().init();
        while (true) {
            desktopApp.process();
            Thread.sleep(2_000);
        }
    }
}
