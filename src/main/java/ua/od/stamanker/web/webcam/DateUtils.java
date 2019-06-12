package ua.od.stamanker.web.webcam;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    public static final SimpleDateFormat DATE_FORMAT_DAY_HOUR = new SimpleDateFormat("yyyy-MM-dd__HH");

    public static String getCurrentHour() {
        return DATE_FORMAT_DAY_HOUR.format(new Date());
    }

    public static String getCurrentDateTime() {
        return DATE_FORMAT.format(new Date());
    }
}
