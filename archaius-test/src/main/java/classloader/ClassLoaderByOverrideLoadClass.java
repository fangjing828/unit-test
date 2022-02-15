package classloader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangjing 2022-01-10.
 */
public class ClassLoaderByOverrideLoadClass extends ClassLoader {
    private final ClassLoader jdkClassLoader;
    private final Map<String, String> classFilePath = new HashMap<>();

    public ClassLoaderByOverrideLoadClass(ClassLoader jdkClassLoader) {
        this.jdkClassLoader = jdkClassLoader;
        classFilePath.put("classloader.ClassLoaderIsolationA", "/Users/fangjing07/Documents/project/unit-test/archaius-test/target/classes/classloader/ClassLoaderIsolationA.class");
        classFilePath.put("classloader.ClassLoaderIsolationB", "/Users/fangjing07/Documents/project/unit-test/archaius-test/target/classes/classloader/ClassLoaderIsolationB.class");
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class result = null;
        try {
            //这里要使用 JDK 的类加载器加载 java.lang 包里面的类
            result = jdkClassLoader.loadClass(name);
        } catch (Exception e) {
            //忽略
        }
        if (result != null) {
            return result;
        }

        return loadClassFromFile(name, classFilePath.get(name));
    }

    private Class<?> loadClassFromFile(String name, String filePath) throws ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ClassNotFoundException();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesNumRead;
            while ((bytesNumRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesNumRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (os.size() == 0) {
            throw new ClassNotFoundException();
        }

        byte[] bytes = os.toByteArray();
        return defineClass(name, bytes, 0, bytes.length);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoaderByOverrideLoadClass classLoader = new ClassLoaderByOverrideLoadClass(Thread.currentThread().getContextClassLoader().getParent());
        Class aClass = classLoader.loadClass("classloader.ClassLoaderIsolationA");

        Method mainMethod = aClass.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, new Object[]{args});
        //输出
        //classloader.ClassLoaderIsolationA classloader.ClassLoaderByOverrideLoadClass@5acf9800
        //classloader.ClassLoaderIsolationB classloader.ClassLoaderByOverrideLoadClass@5acf9800
    }
}
