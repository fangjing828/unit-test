package classloader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangjing 2022-01-11.
 */
public class ClassLoaderForStaticVariable extends ClassLoader {
    private Map<String, String> classFilePath = new HashMap<>();

    public ClassLoaderForStaticVariable() {
        classFilePath.put("classloader.StaticVariableIsolationSample", "/Users/fangjing07/Documents/project/unit-test/archaius-test/target/classes/classloader/StaticVariableIsolationSample.class");
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String filePath = classFilePath.get(name);
        return loadClassFromFile(name, filePath);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (!classFilePath.containsKey(name)) {
            return getParent().loadClass(name);
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
}
