package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        CountDownLatch latch = new CountDownLatch(matrixSize);

        class MatrixTask implements Runnable {
            private final int bColumn;

            public MatrixTask(int bColumn) {
                this.bColumn = bColumn;
            }

            @Override
            public void run() {
                columnMultiply(matrixA, matrixB, this.bColumn, matrixC);
                latch.countDown();
            }
        }

        for (int bColumn = 0; bColumn < matrixSize; bColumn++) {
            executor.submit(new MatrixTask(bColumn));
        }
        latch.await();

/*
        // 'ExecutorService.invokeAll' is an alternative way to submit tasks and wait for completion.
        // But this way is slower according to my JMH benchmarks.
        List<Callable<Object>> tasks = IntStream.range(0, matrixSize)
                .mapToObj(bColumn -> (Runnable) (() -> columnMultiply(matrixA, matrixB, bColumn, matrixC)))
                .map(Executors::callable)
                .collect(Collectors.toList());
        executor.invokeAll(tasks);
*/

        return matrixC;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        try {
            for (int bColumn = 0; ; bColumn++) {
                columnMultiply(matrixA, matrixB, bColumn, matrixC);
            }
        } catch (IndexOutOfBoundsException ignored) { }

        return matrixC;
    }

    private static void columnMultiply(int[][] matrixA, int[][] matrixB, int bColumn, int[][] matrixC) {
        final int matrixSize = matrixA.length;
        final int[] thatColumn = new int[matrixSize];

        for (int k = 0; k < matrixSize; k++) {
            thatColumn[k] = matrixB[k][bColumn];
        }

        for (int i = 0; i < matrixSize; i++) {
            final int[] thisRow = matrixA[i];
            int sum = 0;
            for (int k = 0; k < matrixSize; k++) {
                sum += thisRow[k] * thatColumn[k];
            }
            matrixC[i][bColumn] = sum;
        }
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
