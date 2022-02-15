package classloader;

import com.netflix.config.DynamicPropertyFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * Created by fangjing 2022-01-10.
 */
public class ClassIsolationTest {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        StaticVariableIsolationSample.value = System.currentTimeMillis();

        Field filed = StaticVariableIsolationSample.class.getField("value");
        Method method = StaticVariableIsolationSample.class.getDeclaredMethod("getValue");
        filed.setAccessible(true);
        filed.set(StaticVariableIsolationSample.class, 1L);

        System.out.println(method.invoke(StaticVariableIsolationSample.class));

        ClassLoaderForStaticVariable classLoader = new ClassLoaderForStaticVariable();

        Class<?> clazz = classLoader.loadClass(StaticVariableIsolationSample.class.getName());


        StaticVariableIsolationSample x2 = StaticVariableIsolationSample.class.newInstance();
        System.out.println(x2.geStaticValue());

        Field filed1 = clazz.getField("value");
        filed1.setAccessible(true);
        filed1.set(clazz, 2L);
        Method method1 = clazz.getDeclaredMethod("getValue");
        System.out.println(method1.invoke(clazz));
        System.out.println(method.invoke(StaticVariableIsolationSample.class));


        System.out.println(getCodeBase(DynamicPropertyFactory.class));
    }

    public static String getCodeBase(Class<?> cls) {

        if (cls == null) {
            return null;
        }
        ProtectionDomain domain = cls.getProtectionDomain();
        if (domain == null) {
            return null;
        }
        CodeSource source = domain.getCodeSource();
        if (source == null) {
            return null;
        }
        URL location = source.getLocation();
        if (location == null) {
            return null;
        }
        return location.getFile();
    }
}
