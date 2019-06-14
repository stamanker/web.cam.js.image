package ua.od.stamanker.web.webcam;

import org.apache.catalina.*;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * User: maxz
 * Date: 28.12.2015
 */
public class Application {

    private static final Logger log = LoggerFactory.getLogger("MAIN");
    private static final Logger logStart = LoggerFactory.getLogger("START");

    public static String password;

    public static void main(String[] args) throws Exception {
        logStart.info("Start...");
        Tomcat tomcat = new Tomcat();
        readPasswordArgIfSet(args);
        tomcat.setPort(getPort(args));
        File base = new File(System.getProperty("java.io.tmpdir"));
        Context rootCtx = tomcat.addContext("/", base.getAbsolutePath());
        Tomcat.addServlet(rootCtx, "main", new Web());
//        Tomcat.addServlet(rootCtx, "main", new Main());
        //tomcat.getConnector().setMaxHeaderCount(20);
        tomcat.getConnector().setMaxPostSize(100);
        tomcat.getConnector().setAllowTrace(false);
        tomcat.getServer().addLifecycleListener(lifecycleEvent -> {
            //System.out.println("lifecycleEvent = " + lifecycleEvent);
        });
        rootCtx.addServletMappingDecoded("/", "main");
        tomcat.start();
        new Thread(() -> {
            do {
                long freemMemory = Runtime.getRuntime().freeMemory();
                log.info("free memory: " + String.format("%,03d", freemMemory));
                Application.sleep1Min();
            } while(!Thread.currentThread().isInterrupted());
        }).start();
        tomcat.getServer().await();
    }

    public static void sleep1Min() {
        try {
            Thread.sleep(60 * 1000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private static int getPort(String... args) {
        int ret;
        if(args.length==0) {
            ret = 8080;
        } else {
            ret = Integer.parseInt(args[0]);
        }
        log.info("port = " + ret);
        return ret;
    }

    private static void readPasswordArgIfSet(String... args) {
        if(args.length>=2) {
            password = args[1];
        }
        log.info("password = " + password);
    }
}
