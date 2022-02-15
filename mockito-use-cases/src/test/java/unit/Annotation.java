package unit;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;


/**
 * Created by fangjing 2022-02-15.
 */
public class Annotation {
    private static final Logger logger = LoggerFactory.getLogger(Annotation.class);

    @BeforeAll
    static void setup() {
        logger.info("@BeforeAll - executes once before all test methods in this class");
    }

    @BeforeEach
    void init() {
        logger.info("@BeforeEach - executes before each test method in this class");
    }

    @AfterEach
    void tearDown() {
        logger.info("@AfterEach - executed after each test method.");
    }

    @AfterAll
    static void done() {
        logger.info("@AfterAll - executed after all test methods.");
    }

    @DisplayName("Single test successful")
    @Test
    void testSingleSuccessTest() {
        logger.info("Success");
    }

    @Test
    @Disabled("Not implemented yet")
    void testShowSomething() {
    }

    @Test
    void lambdaExpressions() {
        assertTrue(Stream.of(1, 2, 3)
                .mapToInt(i -> i)
                .sum() > 5, "Sum should be greater than 5");
    }

    @Test
    void groupAssertions() {
        int[] numbers = {0, 1, 2, 3, 4};
        assertAll("numbers",
                () -> assertEquals(numbers[0], 1),
                () -> assertEquals(numbers[3], 3),
                () -> assertEquals(numbers[4], 1)
        );
    }


    @Test
    void trueAssumption() {
        assumeTrue(5 > 1);
        assertEquals(5 + 2, 7);
    }

    @Test
    void falseAssumption() {
        assumeFalse(5 < 1);
        assertEquals(5 + 2, 7);
    }

    @Test
    void assumptionThat() {
        String someString = "Just a string";
        assumingThat(
                someString.equals("Just a string"),
                () -> assertEquals(2 + 2, 4)
        );
    }

    @Test
    void shouldThrowException() {
        Throwable exception = assertThrows(UnsupportedOperationException.class, () -> {
            throw new UnsupportedOperationException("Not supported");
        });
        assertEquals(exception.getMessage(), "Not supported");
    }

    @Test
    void assertThrowsException() {
        String str = null;
        assertThrows(IllegalArgumentException.class, () -> {
            Integer.valueOf(str);
        });
    }
}
