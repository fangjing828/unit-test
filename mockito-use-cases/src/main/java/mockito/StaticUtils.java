package mockito;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by fangjing 2021-12-20.
 */
public class StaticUtils {
    private StaticUtils() {}

    public static List<Integer> range(int start, int end) {
        return IntStream.range(start, end)
                .boxed()
                .collect(Collectors.toList());
    }

    public static String name() {
        return "UnitTest";
    }
}
