package pers.fj.unit.test.junit.runner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by fang_j on 2020/08/28.
 */
@RunWith(CustomRunner.class)
public class TestCalculator {
    private Calculator calculator = new Calculator();

    @Test
    public void testAdd() {
        assertEquals("addition", 8, calculator.add(5, 3));
    }
}
