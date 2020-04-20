package ru.javaops.masterjava.matrix;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(100)
public class MatrixBenchmark {
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(MatrixBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void concurrentMultiply(ExecutionPlan plan) throws Exception {
        final int[][] matrixC = MatrixUtil.concurrentMultiply(plan.matrixA, plan.matrixB, plan.executor);
    }

    @Benchmark
    public void singleThreadMultiply(ExecutionPlan plan) {
        final int[][] matrixC = MatrixUtil.singleThreadMultiply(plan.matrixA, plan.matrixB);
    }

    @State(Scope.Benchmark)
    public static class ExecutionPlan {
        private final int threadNumber = 10;

        @Param({"1000"})
        public int matrixSize;

        public int[][] matrixA;
        public int[][] matrixB;
        public ExecutorService executor;

        @Setup(Level.Trial)
        public void doSetup() {
            matrixA = MatrixUtil.create(matrixSize);
            matrixB = MatrixUtil.create(matrixSize);
            executor = Executors.newFixedThreadPool(threadNumber);
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            executor.shutdown();
        }
    }
}