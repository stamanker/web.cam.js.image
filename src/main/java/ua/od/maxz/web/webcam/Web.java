package ua.od.maxz.web.webcam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * User: maxz
 * Date: 28.12.2015
 */
public class Web extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(Web.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info(req.getMethod() + " " + req.getRequestURI() + " from " + req.getRemoteAddr() + ", UserAgent = '" + req.getHeader("USER-AGENT"));
        System.out.println(req.getMethod() + " " + req.getRequestURI() + " from " + req.getRemoteAddr() + ", UserAgent = '" + req.getHeader("USER-AGENT"));
        resp.addHeader("Connection", "close");
        resp.addHeader("Content-Language", "en");
        resp.addHeader("Pragma", "no-cache");
        resp.addHeader("Cache-control", "no-cache, max-age=0, must-revalidate");
//        resp.addHeader("Last-modified", "en");
        //ServletOutputStream outputStream = resp.getOutputStream();
        try (ServletOutputStream outputStream = resp.getOutputStream();) {
            if(req.getRemoteAddr().equals("185.130.5.224")) {
                outputStream.println("banned");
                System.out.println("BANNED: " + req.getRemoteAddr());
            } else {
                if (req.getParameter("image") != null) {
                    outputStream.write(WebCamManager.getInstance().imageFromCam);
                } else {
                    outputStream.print(getPage());
                }
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    private String getPage() {
        return
        "<html>" +
        "<head><title>webcam::</title>" +
//                "<meta http-equiv='refresh' content=\"1;url='/'\"/>" +
        "</head>" +
        "<body bgcolor='black'>" +
        "<img src='/?image' id='img'>" +
        "<script>" +
        "   var number = 0;\n" +
        "   var newImage = new Image();\n" +
        "   newImage.src = \"/?image\";\n" +
        "   function updateImage() {\n" +
        "       if(newImage.complete) {\n" +
        "           document.getElementById(\"img\").src = newImage.src;\n" +
        "           number++;" +
        "           newImage = new Image();\n" +
        "           newImage.src = '/?image&n='+number;\n" +
        "           console.log('=' + newImage.src);" +
        "       }\n" +
        "       setTimeout(updateImage, 500);\n" +
        "   }" +
        "   updateImage();" +
        "</script>" +
        "</body>" +
        "</html>";
    }
}
