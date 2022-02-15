/**
 * Created by fangjing 2022-01-12.
 */
public class Sample {
    static <M extends Animal> M get(Class<M> clazz) {
        Animal animal = new Cat();

        if (!clazz.isInstance(animal)) {
            throw new IllegalArgumentException("Unexpected animal: " + animal);
        }

        // 将父类型对象转换成子类型
        // Cat cat = (Cat) cat;
//        return (M) animal; 等价于 clazz.cast(animal)
        return clazz.cast(animal);
    }

    public static void main(String[] args) {
        Cat cat = get(Cat.class);
    }

}
