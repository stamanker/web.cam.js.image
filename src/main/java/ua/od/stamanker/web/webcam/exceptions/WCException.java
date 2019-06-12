package ua.od.stamanker.web.webcam.exceptions;

/**
 * User: maxz
 * Date: 14.01.2016
 */
public class WCException extends RuntimeException {

    int code;

    public WCException(int code) {
        this.code = code;
    }

    public WCException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
