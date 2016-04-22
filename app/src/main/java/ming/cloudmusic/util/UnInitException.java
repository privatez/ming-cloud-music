package ming.cloudmusic.util;

/**
 * Created by lihaiye on 16/4/22.
 */
public class UnInitException extends RuntimeException {
    public UnInitException() {
    }

    public UnInitException(String detailMessage) {
        super(detailMessage);
    }

    public UnInitException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
