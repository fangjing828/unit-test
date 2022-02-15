import com.netflix.config.DynamicIntProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fangjing 2022-01-12.
 */
public class DynamicIntPropertyWrapper {
    private volatile static Method getMethod;
    private volatile static Method addCallback;
    private volatile static boolean initialized;
    private final Object property;

    public DynamicIntPropertyWrapper(Object property) {
        if (property == null) {
            throw new IllegalArgumentException("Property should not be null");
        }
        this.property = property;
        initialize(property.getClass().getClassLoader());
    }

    public int get() {
        try {
            return (int) getMethod.invoke(property);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException();
        }
    }

    public void addCallback(Runnable callback) {
        try {
            addCallback.invoke(property, callback);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException();
        }
    }

    void initialize(ClassLoader classLoader) {
        if (!initialized) {
            synchronized (DynamicIntPropertyWrapper.class) {
                if (!initialized) {
                    try {
                        Class<?> clazz = classLoader.loadClass(DynamicIntProperty.class.getName());
                        getMethod = clazz.getMethod("get");
                        addCallback = clazz.getMethod("addCallback", Runnable.class);
                        initialized = true;
                    } catch (ClassNotFoundException | NoSuchMethodException e) {
                        throw new RuntimeException("Initialize method failed", e);
                    }
                }
            }
        }
    }
}
