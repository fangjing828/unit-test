package pers.fj.unit.test.benchmark;

import com.google.common.collect.Maps;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by fang_j on 2020/09/11.
 */
public class BenchmarkMapCopy {
    private static HashMap<String, String> map;

    static {
        map = new HashMap<String, String>();
        for (int i = 0; i < 1000; i++) {
            map.put("key" + i, "value" + i);
        }
    }

    @Benchmark
    public void init() {
        HashMap<String, String> clone = Maps.newHashMap(map);
        map = clone;
    }

    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }
}
