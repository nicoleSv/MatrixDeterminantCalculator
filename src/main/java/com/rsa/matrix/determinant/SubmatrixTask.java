package com.rsa.matrix.determinant;

public class SubmatrixTask extends Thread {
    private double[][] matrix;
    private int matrixSize;

    private int startIndex;
    private int endIndex;
    private int threadID;
    private boolean isQuiet;

    private double[] result;

    public SubmatrixTask(double[][] matrix, int matrixSize, int startIndex, int endIndex, int threadID, boolean isQuiet, double[] result) {
        // TODO validation
        this.matrix = matrix;
        this.matrixSize = matrixSize;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.threadID = threadID;
        this.isQuiet = isQuiet;
        this.result = result;
    }

    @Override
    public void run() {
        if(isQuiet) {
            result[threadID] = calculate(matrix, matrixSize, startIndex, endIndex);
        }
        else {
            long startTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + " started.");

            result[threadID] =  calculate(matrix, matrixSize, startIndex, endIndex);

            long endTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + " stopped.");
            System.out.println(Thread.currentThread().getName() + " execution time was (millis) " +
                    (endTime - startTime));
        }
    }

    private double calculate(double[][] matrix, int matrixSize, int start, int end) {
        if(matrixSize == 1) {
            return matrix[0][0];
        }
        else if(matrixSize == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1];
        }
        else {
            double result = 0;
            for(int current = start; current < end; current++) {
                double[][] submatrix = getSubmatrix(matrix, matrixSize, current);

                result += Math.pow(-1, current) *
                        matrix[0][current] *
                        calculate(submatrix, matrixSize-1, 0, submatrix.length);
            }

            return result;
        }
    }

    private double[][] getSubmatrix(double[][] matrix, int matrixSize, int column) {
        double[][] submatrix = new double[matrixSize-1][matrixSize-1];

        int submatrixColumn;
        for(int i = 1; i < matrixSize; i++) {
            submatrixColumn = 0;
            for(int j = 0; j < matrixSize; j++) {
                if(j != column) {
                    submatrix[i-1][submatrixColumn] = matrix[i][j];
                    submatrixColumn++;
                }
            }
        }
        return submatrix;
    }
}
