package classloader;

/**
 * Created by fangjing 2022-01-10.
 */
public class ClassLoaderIsolationB {
    public void print() {
        System.out.println(this.getClass().getName() + " " + this.getClass().getClassLoader());
    }
}
