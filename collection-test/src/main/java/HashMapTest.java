import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by fang_j on 2021/07/12.
 */
public class HashMapTest {
    public static void main(String[] args) {
        Key key1 = new Key("key");
        Key key2 = new Key("KEY");

        Map<Key, Key> map = new HashMap<>();
        map.put(key1, key1);
        System.out.println(map);
        map.put(key2, key2);
        System.out.println(map);
    }


    static class Key {
        private String key;

        public Key(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key key1 = (Key) o;
            return key.equalsIgnoreCase(key1.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key.toLowerCase());
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Key.class.getSimpleName() + "[", "]")
                    .add("key='" + key + "'")
                    .toString();
        }
    }
}
