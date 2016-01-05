package ua.od.maxz.web.webcam;

import com.github.sarxos.webcam.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: maxz
 * Date: 28.12.2015
 */
public class WebCamManager {

    private static final Logger log = LoggerFactory.getLogger(WebCamManager.class);

    private Webcam webcam;
    private WebcamMotionListener listener;
    private static WebCamManager INSTANCE;

    public byte[] imageFromCam;

    Dimension[] nonStandardResolutions = new Dimension[]{
            WebcamResolution.PAL.getSize(),
            WebcamResolution.HD720.getSize(),
            new Dimension(2000, 1000),
            new Dimension(1000, 500),
    };

    public static WebCamManager getInstance() {
        if (INSTANCE == null) {
            synchronized (WebCamManager.class) {
                INSTANCE = new WebCamManager();
            }
        }
        return INSTANCE;
    }

    public WebCamManager() {
        java.util.List<Webcam> webcams = Webcam.getWebcams();
        for (Webcam wc : webcams) {
            System.out.println("wc = " + wc);
        }
        webcam = webcams.get(1);
        webcam.setCustomViewSizes(nonStandardResolutions);
//        webcam.setViewSize(WebcamResolution.HD720.getSize());
//        webcam.setViewSize(WebcamResolution.SVGA.getSize());
        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();

        WebcamMotionDetector detector = new WebcamMotionDetector(Webcam.getDefault());
        detector.setInterval(5000); // one check per xxx ms
        listener = new WebcamMotionListener() {
            @Override
            public void motionDetected(WebcamMotionEvent webcamMotionEvent) {
//                takeImage();
            }
        };
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(this::takeImage, 0, 200, TimeUnit.MILLISECONDS);
        detector.addMotionListener(listener);
        detector.start();
    }

    private void takeImage() {
        try {
            BufferedImage photo = webcam.getImage();
            imageFromCam = getPicAsBytes(photo);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    private byte[] getPicAsBytes(BufferedImage photo) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(photo, "jpg", baos);
            baos.flush();
            return baos.toByteArray();
        }
    }

}
