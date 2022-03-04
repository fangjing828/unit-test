package unit;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Created by fangjing 2022-02-15.
 */
public class DynamicTests {
//    @TestFactory
//    public Stream<DynamicTest> translateDynamicTestsFromStream() {
//        return in.stream()
//                .map(word ->
//                        DynamicTest.dynamicTest("Test translate " + word, () -> {
//                            int id = in.indexOf(word);
//                            assertEquals(out.get(id), translate(word));
//                        })
//                );
    }
}
