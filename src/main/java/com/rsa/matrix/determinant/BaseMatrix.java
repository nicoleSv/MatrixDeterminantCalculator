package com.rsa.matrix.determinant;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class BaseMatrix implements Matrix {
    protected double[][] matrix;
    protected int matrixSize;
    protected boolean isQuiet;
    protected BigDecimal determinant;

    public BaseMatrix() {

    }

    public BaseMatrix(boolean isQuiet) {
        this.isQuiet = isQuiet;
//        this.matrix = setupMatrix();
    }

    public abstract double[][] setupMatrix();

    public BigDecimal getDeterminant(int threadsCount) {
        final int nThreads = threadsCount <= matrixSize ? threadsCount : matrixSize;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        int[] permVector = new int[matrix.length];
        final AtomicBoolean sign = new AtomicBoolean(true);
        for(int i = 0; i < matrix.length; i++) {
            permVector[i] = i;
        }

        final List<Callable<Void>> tasks = new LinkedList<>();
        for (int jIndex = 0; jIndex < matrix.length - 1; jIndex++) {
            final int j = jIndex;
            tasks.add( () -> { // Callable<Void>

                long startTime = System.currentTimeMillis();
                long threadID = Thread.currentThread().getId() % nThreads;
                if(!isQuiet)
                    System.out.println("Thread-" + threadID + " started.");

                // Find pivot element in the j-th column.
                double max = Math.abs(matrix[j][j]);
                int i_pivot = j;
                for (int i = j + 1; i < matrix.length; ++i) {
                    double aij = Math.abs(matrix[i][j]);
                    if (aij > max) {
                        max = aij;
                        i_pivot = i;
                    }
                }
                if (i_pivot != j) {
                    // because this is a multiple action block, it must be synchronized - swap pivots
                    synchronized (tasks) {
                        double[] swap = matrix[i_pivot];
                        matrix[i_pivot] = matrix[j];
                        matrix[j] = swap;
                    }
                    // same with this
                    synchronized (sign) {
                        // update permutation
                        int swap2 = permVector[i_pivot];
                        permVector[i_pivot] = permVector[j];
                        permVector[j] = swap2;
                        synchronized (sign) {
                            sign.set(!sign.get());
                        }
                    }
                }

                long endTime = System.currentTimeMillis();
                if(!isQuiet) {
                    System.out.println("Thread-" + threadID + " stopped.");
                    System.out.println("Thread-" + threadID + " execution time was (millis) " +
                            (endTime - startTime));
                }
                return null;
            });
        }
        try {
            executor.invokeAll(tasks);
            for (int j = 0; j < matrix.length - 1; ++j) {
                double pivot = matrix[j][j];
                if (pivot != 0.0) {
                    // calculate decomposition based on the pivot if not 0
                    for (int i = j + 1; i < matrix.length; ++i) {
                        final double v = matrix[i][j] / pivot;
                        matrix[i][j] = v;
                        for (int k = j + 1; k < matrix.length; ++k)
                            matrix[i][k] -= v * matrix[j][k];
                    }
                } else {
                    throw new ArithmeticException("Pivot cannot be 0, aborting!");
                }
            }
            determinant = new BigDecimal(sign.get() ? 1 : -1);
            // go through the decomposed matrix diagonally and multiply to find the determinant
            for (int i = 0; i < matrix.length; ++i) {
                BigDecimal element = new BigDecimal(matrix[i][i]);
                determinant = determinant.multiply(element);
            }
                //determinant *= matrix[i][i];
        } catch (InterruptedException e) {
            System.err.println("Problem with multithreading!");
            e.printStackTrace();
        }
        finally {
            executor.shutdown();
        }

        System.out.println("Threads used in current run: " + threadsCount);
        determinant = determinant.setScale(2, RoundingMode.HALF_UP);
        return this.determinant;
    }

    public void saveResult(File resultFile) {
        if(!resultFile.isFile()) {
            System.err.println("No such file.");
            return;
        }

        try {
            Writer writer = new BufferedWriter(new FileWriter(resultFile));
            writer.write(String.format("detA = %.3E", this.determinant));
            writer.close();
        }
        catch(IOException e){
            System.err.println("Could not write in file.");
            e.printStackTrace();
        }
    }

    public void printMatrix() {
        StringBuilder builder = new StringBuilder();

        for(int row = 0; row < matrixSize; row++) {
            double[] matrixRow = matrix[row];
            builder.append("[");

            for(int col = 0; col < matrixRow.length; col++) {
                builder.append(" ");
                builder.append(matrixRow[col]);
            }
            builder.append(" ]\n");
        }

        System.out.println(builder.toString());
    }
}