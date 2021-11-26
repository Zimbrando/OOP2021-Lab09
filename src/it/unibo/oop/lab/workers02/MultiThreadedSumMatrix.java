package it.unibo.oop.lab.workers02;

import java.util.Arrays;
import java.util.stream.IntStream;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;
    
    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }
    
    
    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private long res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from column " + startpos + " to column " + (startpos + nelem - 1));
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {
                Arrays.stream(matrix[i]).forEach(c -> {
                    res += c;
                });
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public long getResult() {
            return this.res;
        }

    }
    
    @Override
    public double sum(double[][] matrix) {
        final int rows = matrix.length % nthread + matrix.length / nthread;

        return IntStream.iterate(0, start -> start + rows) 
                .limit(nthread)
                .mapToObj(start -> new Worker(matrix, start, rows))
                .peek(Thread::start)
                .peek(MultiThreadedSumMatrix::joinUninterruptibly)
                .mapToLong(Worker::getResult)
                .sum();
    }
    
    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
