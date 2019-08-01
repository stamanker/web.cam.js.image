package ua.od.stamanker.web.webcam;

import com.github.sarxos.webcam.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: maxz
 * Date: 28.12.2015
 */
public class WebCamManager {

    private static final Logger log = LoggerFactory.getLogger(WebCamManager.class);

    private Webcam webcam;
    private static volatile WebCamManager INSTANCE;

    public AtomicReference<byte[]> image = new AtomicReference<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    Dimension[] nonStandardResolutions = new Dimension[]{
            new Dimension(640, 480),
            WebcamResolution.PAL.getSize(),
            //WebcamResolution.HD720.getSize(),
            //new Dimension(2000, 1000),
            //new Dimension(1000, 500),
    };

    public static WebCamManager getInstance() {
        if (INSTANCE == null) {
            synchronized (WebCamManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WebCamManager();
                }
            }
        }
        return INSTANCE;
    }

    private WebCamManager() {
        List<Webcam> webcams = Webcam.getWebcams();
        for (Webcam webcam : webcams) {
            log.info("cam = " + webcam);
        }
        webcam = webcams.get(webcams.size() - 1);
        webcam.setCustomViewSizes(nonStandardResolutions);
//        webcam.setViewSize(WebcamResolution.HD720.getSize());
//        webcam.setViewSize(WebcamResolution.SVGA.getSize());
        for (int i = 0; i < 2; i++) {
            try {
                webcam.setViewSize(new Dimension(640, 480));
                break;
            } catch (Exception e) {
                webcam.close();
            }
        }
        webcam.open();

//        WebcamMotionDetector detector = new WebcamMotionDetector(Webcam.getDefault());
//        detector.setInterval(100); //100 is minimum
//        WebcamMotionListener listener = event -> takeImage();
//        detector.addMotionListener(listener);
//        System.out.println(detector.getMaxMotionPoints());
//        detector.setMaxMotionPoints(10);
//        detector.start();

        // --- auto save picture
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(this::takeImage, 0, 150, TimeUnit.MILLISECONDS);
    }

    private void takeImage() {
        try {
            long start = System.currentTimeMillis();
            BufferedImage photo = webcam.getImage();
//            System.out.println("*** WebCamManager.takeImage1 took " + String.format("%,3d", System.currentTimeMillis() - start));
            executorService.execute(() -> {
                try {
                    byte[] picAsBytes = getPicAsBytes(photo);
                    image.set(picAsBytes);
//                    System.out.println("*** WebCamManager.takeImage3 took " + String.format("%,3d", System.currentTimeMillis() - start));
//                    File pixMainDir = new File("pix");
//                    if (!pixMainDir.exists()) {
//                        pixMainDir.mkdir();
//                    }
//                    String subDir = "pix/" + DateUtils.getCurrentHour() + "/";
//                    File file = new File(subDir);
//                    if (!file.exists()) {
//                        file.mkdir();
//                    }
//                    Files.write(
//                            Paths.get(subDir + DateUtils.getCurrentDateTime() + ".jpg"),
//                            picAsBytes,
//                            StandardOpenOption.CREATE_NEW, StandardOpenOption.CREATE, StandardOpenOption.WRITE
//                    );
                } catch (Exception e) {
                    log.error("Error: " + e.getMessage(), e);
                }
                System.out.println("*** WebCamManager.takeImageX took " + String.format("%,3d", System.currentTimeMillis() - start));
            });
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
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
