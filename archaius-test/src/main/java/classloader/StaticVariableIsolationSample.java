package classloader;

/**
 * Created by fangjing 2022-01-10.
 */
public class StaticVariableIsolationSample {
    public static volatile long value;

    public static long getValue() {
        return value;
    }

    public long geStaticValue() {
        return value;
    }
}
