package ua.od.stamanker.web.webcam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.od.stamanker.web.webcam.exceptions.WCException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: maxz
 * Date: 28.12.2015
 */
public class Web extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(Web.class);
    private static Set<String> banned = new HashSet<>(Arrays.asList(
            "185.130.5.224", "38.89.139.16", "217.160.128.25", "123.56.108.130", "118.98.104.21",
            "115.230.124.164", "80.82.64.68"
    )
    );

    @Override
    public void init() throws ServletException {
        log.debug("...");
        super.init();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.debug("...");
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!req.getRemoteAddr().equals("0:0:0:0:0:0:0:1")) {
            log.info(req.getMethod() + " " + req.getRequestURI() + " from " + req.getRemoteAddr() + ", UserAgent = '" + req.getHeader("USER-AGENT") + "'");
        }
        try (ServletOutputStream outputStream = resp.getOutputStream()) {
            checkBan(req);
            if (req.getParameter("image") != null) {
                resp.addHeader("Content-type", "image/jpeg");
                byte[] imageFromCam = WebCamManager.getInstance().image.get();
                if (imageFromCam != null) {
                    outputStream.write(imageFromCam);
                } else {
                    resp.sendError(500, "no image somewhy...");
                }
            } else {
                // --- bad but simple
                if (Application.password == null || !req.getSession().isNew()) {
                    setHeaders(resp);
                    outputStream.print(getPage());
                } else {
                    if (Application.password.equals(req.getParameter("pswd"))) {
                        resp.sendRedirect("/");
                    } else {
                        throw new WCException(HttpServletResponse.SC_FORBIDDEN);
                    }
                }
            }
        } catch (WCException we) {
            log.error("Error: " + we.getMessage());
            req.getSession().invalidate();
            resp.sendError(we.getCode(), we.getMessage());
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    private void setHeaders(HttpServletResponse resp) {
        resp.addHeader("Connection", "close");
        resp.addHeader("Content-Language", "en");
        resp.addHeader("Pragma", "no-cache");
        resp.addHeader("Content-type", "text/html");
        resp.addHeader("Cache-control", "no-cache, max-age=0, must-revalidate");
    }

    private void checkBan(HttpServletRequest req) {
        if (banned.contains(req.getRemoteAddr())) {
            log.info("BANNED: " + req.getRemoteAddr());
            throw new WCException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String getPage() {
        return
                "<html>" +
                        "<head><meta keywords='webcam'><title>webcam::</title>" +
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
                        "       setTimeout(updateImage, 300);\n" +
                        "   }" +
                        "   updateImage();" +
                        "</script>" +
                        "</body>" +
                        "</html>";
    }
}
