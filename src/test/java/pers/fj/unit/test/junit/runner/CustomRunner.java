package pers.fj.unit.test.junit.runner;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Method;

/**
 * Created by fang_j on 2020/08/28.
 */
public class CustomRunner extends BlockJUnit4ClassRunner {
    private final Class<?> clazz;
    public CustomRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
    }

    @Override
    public Description getDescription() {
        return Description
                .createTestDescription(clazz, "My runner description");
    }

    @Override
    public void run(RunNotifier notifier) {
        System.out.println("running the tests from MyRunner: " + clazz);
        try {
            Object testObject = clazz.newInstance();
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                    notifier.fireTestStarted(Description
                            .createTestDescription(clazz, method.getName()));
                    method.invoke(testObject);
                    notifier.fireTestFinished(Description
                            .createTestDescription(clazz, method.getName()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
