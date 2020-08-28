package pers.fj.unit.test.junit.parameterized;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class FibonacciTest {
    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][]{
                {0, 0},
                {1, 1},
                {2, 1},
                {3, 2},
                {4, 3},
                {5, 5},
                {6, 8}
        };
    }

    private int fInput;

    private int fExpected;

    public FibonacciTest(int input, int expected) {
        fInput = input;
        fExpected = expected;
    }

    private Fibonacci fibonacci;

    @Before
    public void setUp() {
        fibonacci = new Fibonacci();
    }

    @Test
    public void testCompute() {
        assertEquals(fExpected, fibonacci.compute(fInput));
    }
}