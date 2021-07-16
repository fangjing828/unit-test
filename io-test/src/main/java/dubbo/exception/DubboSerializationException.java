package dubbo.exception;

/**
 * Created by fang_j on 2021/07/16.
 */
public class DubboSerializationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DubboSerializationException(String message, Throwable ex) {
        super(message, ex);
    }
}
