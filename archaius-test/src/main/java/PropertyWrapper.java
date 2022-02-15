import java.lang.reflect.Method;

/**
 * Created by fangjing 2022-01-12.
 */
public abstract class PropertyWrapper {
    private volatile static Method getMethod;
    private volatile static Method addCallbackMethod;

    static void initAddCallback(Class<?> clazz) {
        if (clazz.getName().equals(com.netflix.config.PropertyWrapper.class.getName())) {
            throw new IllegalArgumentException("Expected the class name：" + com.netflix.config.PropertyWrapper.class.getName());
        }
        if (clazz.getClassLoader() instanceof ArchaiusClassLoader) {
            throw new IllegalArgumentException("Expected the class loader type of class is: " + ArchaiusClassLoader.class.getName());
        }
        if (addCallbackMethod != null) {
            synchronized (PropertyWrapper.class) {
                try {
                    addCallbackMethod = clazz.getMethod("addCallback", Runnable.class);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
