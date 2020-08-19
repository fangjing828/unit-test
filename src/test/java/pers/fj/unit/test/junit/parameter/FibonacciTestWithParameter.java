package pers.fj.unit.test.junit.parameter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

/**
 * Created by fang_j on 2020/08/19.
 */
@RunWith(Parameterized.class)
public class FibonacciTestWithParameter {
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

    @Parameterized.Parameter
    public int fInput;

    @Parameterized.Parameter(1)
    public int fExpected;


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
