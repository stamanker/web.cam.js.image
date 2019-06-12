package ua.od.stamanker.web.webcam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * User: maxz
 * Date: 28.12.2015
 */
public class Listener implements ServletContextListener, HttpSessionListener {

    private static final Logger log = LoggerFactory.getLogger(Listener.class);

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.debug("servletContextEvent = [" + servletContextEvent + "]");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.debug("servletContextEvent = [" + servletContextEvent + "]");
        WebCamManager.getInstance();
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        log.debug("event = " + httpSessionEvent);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

    }
}
