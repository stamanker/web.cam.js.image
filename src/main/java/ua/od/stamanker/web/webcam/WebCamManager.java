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
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * User: maxz
 * Date: 28.12.2015
 */
public class WebCamManager {

    private static final Logger log = LoggerFactory.getLogger(WebCamManager.class);

    private Webcam webcam;
    private WebcamMotionListener listener;
    private static WebCamManager INSTANCE;

    public byte[] imageFromCam; // synchronization or something required

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
        webcam = webcams.get(webcams.size()-1);
        webcam.setCustomViewSizes(nonStandardResolutions);
//        webcam.setViewSize(WebcamResolution.HD720.getSize());
//        webcam.setViewSize(WebcamResolution.SVGA.getSize());
        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();

        WebcamMotionDetector detector = new WebcamMotionDetector(Webcam.getDefault());
        detector.setInterval(1_000); // one check per xxx ms
        listener = webcamMotionEvent -> {
                takeImage();
        };
        detector.addMotionListener(listener);
        detector.start();
        // --- auto save picture
//        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
//        executorService.scheduleWithFixedDelay(this::takeImage, 0, 400, TimeUnit.MILLISECONDS);
    }

    private void takeImage() {
        try {
            BufferedImage photo = webcam.getImage();
            imageFromCam = getPicAsBytes(photo);
            File pixMainDir = new File("pix");
            if(!pixMainDir.exists()) {
                pixMainDir.mkdir();
            }
            String subDir = "pix/" + DateUtils.getCurrentHour() + "/";
            File file = new File(subDir);
            if(!file.exists()) {
                file.mkdir();
            }
            Files.write(
                    Paths.get(subDir + DateUtils.getCurrentDateTime()+".jpg"),
                    imageFromCam,
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.CREATE, StandardOpenOption.WRITE
            );
        } catch (Exception e) {
            log.error("Error: " + e.getMessage(), e);
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
