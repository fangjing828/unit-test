package classloader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangjing 2022-01-10.
 */
public class ClassLoaderByOverrideFindClass extends ClassLoader{
    private Map<String, String> classFilePath = new HashMap<>();

    public ClassLoaderByOverrideFindClass() {
        classFilePath.put("classloader.ClassLoaderIsolationA", "/Users/fangjing07/Documents/project/unit-test/archaius-test/target/classes/classloader/ClassLoaderIsolationA.class");
        classFilePath.put("classloader.ClassLoaderIsolationB", "/Users/fangjing07/Documents/project/unit-test/archaius-test/target/classes/classloader/ClassLoaderIsolationB.class");
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String filePath = classFilePath.get(name);
        return loadClassFromFile(name, filePath);
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
        ClassLoaderByOverrideFindClass classLoader = new ClassLoaderByOverrideFindClass();
        Class aClass = classLoader.findClass("classloader.ClassLoaderIsolationA");

        Method mainMethod = aClass.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, new Object[]{args});

       // classloader.ClassLoaderIsolationA classloader.ClassLoaderByOverrideFindClass@5acf9800
       // classloader.ClassLoaderIsolationB sun.misc.Launcher$AppClassLoader@18b4aac2


        // ClassLoaderIsolationA 使用的是 ClassLoaderByOverrideFindClass 加载的
        // ClassLoaderIsolationB 还是使用 ClassLoaderByOverrideFindClass 加载的

        // JVM 在触发类加载时调用的是 ClassLoader.loadClass 方法。这个方法的实现了双亲委派：
        // 1. 委托给父加载器查询
        // 2. 如果父加载器查询不到，就调用 findClass 方法进行加载
    }
}
