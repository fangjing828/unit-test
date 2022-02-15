package classloader;

/**
 * Created by fangjing 2022-01-10.
 */
public class ClassLoaderIsolationA {
    public static void main(String[] args) {
        new ClassLoaderIsolationA().print();
    }

    public void print() {
        System.out.println(this.getClass().getName() + " " + this.getClass().getClassLoader());
        new ClassLoaderIsolationB().print();
    }
}
