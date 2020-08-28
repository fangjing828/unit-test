package pers.fj.unit.test.junit.categories;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Created by fang_j on 2020/08/28.
 */
public class TestSample {
    @Test
    @Category(FeatureA.class)
    public void testAdd() {
        System.out.println("A.testAdd");
    }

    @Test
    @Category(FeatureB.class)
    public void testAdd2() {
        System.out.println("B.testAdd2");
    }

    @Test
    @Category({FeatureA.class, FeatureB.class})
    public void testAdd3() {
        System.out.println("A/B.testAdd3");
    }

    @Test
    public void testAdd4() {
        System.out.println("testAdd4");
    }
}
