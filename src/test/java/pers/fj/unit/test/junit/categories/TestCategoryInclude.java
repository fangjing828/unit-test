package pers.fj.unit.test.junit.categories;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by fang_j on 2020/08/28.
 */
@RunWith(Categories.class)
@Categories.IncludeCategory(FeatureA.class)
@Suite.SuiteClasses({ TestSample.class, /*Any test class you want to run*/})
public class TestCategoryInclude {
}
