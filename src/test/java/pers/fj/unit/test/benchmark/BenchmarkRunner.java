package pers.fj.unit.test.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by fang_j on 2020/08/28.
 */
public class BenchmarkRunner {
    @Benchmark
    public void init() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1);
    }

    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }
}
