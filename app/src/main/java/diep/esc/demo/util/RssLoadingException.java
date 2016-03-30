package diep.esc.demo.util;

/**
 * Created by Diep on 29/03/2016.
 */
public class RssLoadingException extends RuntimeException {
    public RssLoadingException() {
    }

    public RssLoadingException(String detailMessage) {
        super(detailMessage);
    }

    public RssLoadingException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RssLoadingException(Throwable throwable) {
        super(throwable);
    }
}
