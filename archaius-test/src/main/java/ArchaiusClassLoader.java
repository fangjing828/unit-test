import com.netflix.config.DynamicPropertyFactory;

import java.io.*;
import java.net.*;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Created by fangjing 2022-01-07.
 */
public class ArchaiusClassLoader extends URLClassLoader {
    private final Map<String, Class<?>> loadedClasses = new HashMap<>();

    public ArchaiusClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (!name.startsWith("com.netflix.config.")) {
            return super.loadClass(name, resolve);

        }
        System.out.println(name);

        if (!loadedClasses.containsKey(name)) {
            definePackageIfNecessary(name);
            synchronized (loadedClasses) {
                if (!loadedClasses.containsKey(name)) {
                    loadedClasses.put(name, defineClass(name));
                }
            }
        }

       return loadedClasses.get(name);
    }

    /**
     * Define a package before a {@code findClass} call is made. This is necessary to
     * ensure that the appropriate manifest for nested JARs is associated with the
     * package.
     *
     * @param className the class name being found
     */
    private void definePackageIfNecessary(String className) {
        int lastDot = className.lastIndexOf('.');
        if (lastDot >= 0) {
            String packageName = className.substring(0, lastDot);
            if (getPackage(packageName) == null) {
                try {
                    definePackage(className, packageName);
                } catch (IllegalArgumentException ex) {
                    // Tolerate race condition due to being parallel capable
                }
            }
        }
    }

    private void definePackage(final String className, final String packageName) {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                StringBuilder pen = new StringBuilder(packageName.length() + 10);
                StringBuilder cen = new StringBuilder(className.length() + 10);
                String packageEntryName = pen.append(packageName.replace('.', '/')).append("/")
                        .toString();
                String classEntryName = cen.append(className.replace('.', '/'))
                        .append(".class").toString();
                for (URL url : getURLs()) {
                    try {
                        URLConnection connection = url.openConnection();
                        if (connection instanceof JarURLConnection) {
                            JarFile jarFile = ((JarURLConnection) connection).getJarFile();
                            if (jarFile.getEntry(classEntryName) != null
                                    && jarFile.getEntry(packageEntryName) != null
                                    && jarFile.getManifest() != null) {
                                definePackage(packageName, jarFile.getManifest(), url);
                                return null;
                            }
                        }
                    } catch (IOException ex) {
                        // Ignore
                    }
                }
                return null;
            }, AccessController.getContext());
        } catch (java.security.PrivilegedActionException ex) {
            // Ignore
        }
    }

    private Class<?> defineClass(final String className) throws ClassNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>) () -> {
                StringBuilder cen = new StringBuilder(className.length() + 10);
                String classEntryName = cen.append(className.replace('.', '/'))
                        .append(".class").toString();
                for (URL url : getURLs()) {
                    try {
                        URLConnection connection = url.openConnection();
                        if (connection instanceof JarURLConnection) {
                            JarFile jarFile = ((JarURLConnection) connection).getJarFile();
                            if (jarFile.getEntry(classEntryName) != null
                                    && jarFile.getManifest() != null) {
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                try (InputStream is = jarFile.getInputStream(jarFile.getEntry(classEntryName))) {
                                    byte[] buffer = new byte[4096];
                                    int bytesNumRead;
                                    while ((bytesNumRead = is.read(buffer)) != -1) {
                                        os.write(buffer, 0, bytesNumRead);
                                    }
                                    byte[] bytes = os.toByteArray();
                                    return defineClass(className, bytes, 0, bytes.length);
                                }
                            }
                        }
                    } catch (IOException ex) {
                        // Ignore
                    }
                }
                return null;
            }, AccessController.getContext());
        } catch (java.security.PrivilegedActionException ex) {
            // Ignore
        }
        throw new ClassNotFoundException(className);
    }

    static ArchaiusClassLoader getInstance() {
        final String s = getCodeBase(DynamicPropertyFactory.class);
        File[] path = (s == null) ? new File[0] : new File[]{new File(s)};
        URL[] urls = pathToURLs(path);

        return new ArchaiusClassLoader(urls, Thread.currentThread().getContextClassLoader());
    }

    private static String getCodeBase(Class<?> cls) {
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

    private static URL[] pathToURLs(File[] path) {
        URL[] urls = new URL[path.length];
        for (int i = 0; i < path.length; i++) {
            urls[i] = getFileURL(path[i]);
        }
        return urls;
    }

    private static URL getFileURL(File file) {
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
        }

        try {
            return new URL(String.format("jar:file:%s!/", file.getPath()));
        } catch (MalformedURLException e) {
            // Should never happen since we specify the protocol...
            throw new InternalError(e);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        ArchaiusClassLoader.getInstance().loadClass(DynamicPropertyFactory.class.getName());
    }
}


