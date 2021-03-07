package ua.od.stamanker.web.webcam;

import com.github.sarxos.webcam.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: maxz
 * Date: 28.12.2015
 */
public class WebCamManager {

    private static final Logger log = LoggerFactory.getLogger(WebCamManager.class);

    private final List<Webcam> webcams = new LinkedList<>();
    private static volatile WebCamManager INSTANCE;

    public AtomicReference<byte[]> image = new AtomicReference<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

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
        List<Webcam> webcamsLocal = Webcam.getWebcams();
        for (Webcam webcam : webcamsLocal) {
            log.info("cam = " + webcam);
            System.out.println("webcam = " + webcam);
//            if (webcam.getName().startsWith("HP ")) {
//                System.out.println("ignore " + webcam.getName());
//                continue;
//            }
            if (!webcam.getName().startsWith("HD Web Camera")) {
                System.out.println("ignore " + webcam.getName());
                continue;
            }
            webcams.add(webcam);
            webcam.setCustomViewSizes(nonStandardResolutions);
            for (int i = 0; i < 2; i++) {
                try {
//        webcam.setViewSize(WebcamResolution.HD720.getSize());
//        webcam.setViewSize(WebcamResolution.SVGA.getSize());
                    webcam.setViewSize(new Dimension(640, 480));
                    break;
                } catch (Exception e) {
                    webcam.close();
                }
            }
            webcam.open();
        }

//        WebcamMotionDetector detector = new WebcamMotionDetector(Webcam.getDefault());
//        detector.setInterval(500); //100 is minimum
//        WebcamMotionListener listener = event -> takeImage();
//        detector.addMotionListener(listener);
//        System.out.println(detector.getMaxMotionPoints());
//        detector.setMaxMotionPoints(10);
//        detector.start();

        // --- auto save picture
//        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
//        executorService.scheduleWithFixedDelay(this::takeImage, 0, 800, TimeUnit.MILLISECONDS);
    }

    private long switchTime = System.currentTimeMillis();
    private int switchMe = 0;

    public byte[] getImage() {
        try {
//            if (System.currentTimeMillis() - switchTime > 5_000) {
//                switchMe = (++switchMe) % 2;
//                switchTime = System.currentTimeMillis();
//            }
//            if (switchMe == 0) {
                return getPicAsBytes(webcams.get(0).getImage());
//            } else {
//                return getPicAsBytes(webcams.get(1).getImage());
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void takeImage() {
        try {
            long start = System.currentTimeMillis();
            BufferedImage photo = webcams.get(0).getImage();
            System.out.println("*** WebCamManager.takeImage1 took " + String.format("%,3d", System.currentTimeMillis() - start));
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
                //System.out.println("*** WebCamManager.takeImageX took " + String.format("%,3d", System.currentTimeMillis() - start));
            });
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
        }
    }

    private byte[] getPicAsBytes(BufferedImage photo) throws IOException {
        JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(0.7f);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(photo, "jpg", baos);
        final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        // specifies where the jpg image has to be written
        writer.setOutput(ImageIO.createImageOutputStream(baos));
        // writes the file with given compression level
        // from your JPEGImageWriteParam instance
        writer.write(null, new IIOImage(photo, null, null), jpegParams);
        return baos.toByteArray();
    }

}
