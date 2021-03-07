package ua.od.stamanker.web.webcam;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Time;
import java.util.Date;

public class ImageSender {

    public ImageSender process() {
        long total = System.currentTimeMillis();
        Socket socket = new Socket();
        //long sendStart = 0;
        try {
            byte[] image = getImage();
            byte[] header = new byte[16];
            header[0] = 23;
            for (int i = 1; i < 6; i++) {
                header[i] = (byte) i;
            }
            socket.setSendBufferSize(1024*65);
            socket.setSoLinger(false, 1);
            connect(socket);
            socket.setTcpNoDelay(false);
            //sendStart = System.currentTimeMillis();
            try (OutputStream outputStream = socket.getOutputStream()) {
                outputStream.write(header);
                outputStream.write(image);
                outputStream.flush();
            }
            //System.out.println(new Date() + " Send");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(socket);
            //System.out.println("*** send took " + String.format("%,3d", System.currentTimeMillis() - sendStart));
            System.out.println(new Date() + " *** TOTAL took " + String.format("%,3d", System.currentTimeMillis() - total));
        }
        return this;
    }

    private void connect(Socket socket) throws IOException {
        long start = System.currentTimeMillis();
        socket.connect(new InetSocketAddress("127.0.0.1", 8082));
        //System.out.println("*** connect took " + String.format("%,3d", System.currentTimeMillis() - start));
    }

    private byte[] getImage() {
        long start = System.currentTimeMillis();
        byte[] image = WebCamManager.getInstance().getImage();
        System.out.println("image = " + image.length);
        System.out.println("*** takeImage took " + String.format("%,3d", System.currentTimeMillis() - start));
//        try (OutputStream outputStream = Files.newOutputStream(Paths.get("x.png"), StandardOpenOption.CREATE)){
//            outputStream.write(image);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if(image==null) {
            throw new IllegalStateException("no image");
        }
        return image;
    }

    private void close(Socket socket) {
        if (socket.isConnected()) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ImageSender desktopApp = new ImageSender();
        WebCamManager.getInstance();
        Thread.sleep(100);
        while (true) {
            desktopApp.process();
            Thread.sleep(1000);
        }
    }
}
