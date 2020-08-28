package pers.fj.unit.test.junit.parameterized;

/**
 * Created by fang_j on 2020/08/19.
 */
public class Fibonacci {
    int compute(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n should be greater than zero");
        }
        if (n == 0) return 0;

        int x = 1;
        int y = 1;
        int result = 1;
        for (int i = 3; i <= n; i++) {
            result = x + y;

            x = y;
            y = result;
        }

        return result;
    }
}
