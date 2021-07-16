package dubbo.exception;

/**
 * Created by fang_j on 2021/07/16.
 */
public class DubboException extends Exception {
    public DubboException(String message, Throwable cause) {
        super(message, cause);
    }
}
