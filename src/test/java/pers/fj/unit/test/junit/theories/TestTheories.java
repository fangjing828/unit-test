package pers.fj.unit.test.junit.theories;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * Created by fang_j on 2020/08/28.
 */
@RunWith(Theories.class)
public class TestTheories {
    @DataPoint
    public static String nameValue1 = "Tony";
    @DataPoint
    public static String nameValue2 = "Jim";
    @DataPoint
    public static String nameValue3 = "Sam";
    @DataPoint
    public static int ageValue1 = 10;
    @DataPoint
    public static int ageValue2 = 20;

    @Theory
    public void testMethod(String name, int age){
        System.out.println(String.format("%s's age is %s", name, age));
    }
}
