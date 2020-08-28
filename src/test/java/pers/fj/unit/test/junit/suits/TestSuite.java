package pers.fj.unit.test.junit.suits;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import pers.fj.unit.test.junit.parameterized.FibonacciTest;
import pers.fj.unit.test.junit.parameterized.FibonacciTestWithCustomName;
import pers.fj.unit.test.junit.parameterized.FibonacciTestWithParameter;

/**
 * Created by fang_j on 2020/08/28.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({FibonacciTest.class, FibonacciTestWithCustomName.class, FibonacciTestWithParameter.class})
public class TestSuite {
}
