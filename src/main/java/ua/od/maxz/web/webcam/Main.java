package ua.od.maxz.web.webcam;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * User: maxz
 * Date: 28.12.2015
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger("MAIN");
    private static final Logger logStart = LoggerFactory.getLogger("START");

    public static void main(String[] args) throws Exception {
        logStart.info("Start...");
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(getPort(args));
        File base = new File(System.getProperty("java.io.tmpdir"));
        Context rootCtx = tomcat.addContext("/", base.getAbsolutePath());
        Tomcat.addServlet(rootCtx, "main", new Web());
//        Tomcat.addServlet(rootCtx, "main", new Main());
        rootCtx.addServletMapping("/", "main");
        tomcat.start();
        tomcat.getServer().await();
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
}
