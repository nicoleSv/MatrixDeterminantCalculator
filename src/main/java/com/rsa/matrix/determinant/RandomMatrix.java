package com.rsa.matrix.determinant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class RandomMatrix extends BaseMatrix {
    private final static int MIN = 0;
    private final static int MAX = 30;

    public RandomMatrix(int matrixSize, boolean isQuiet) {
        super(isQuiet);

        this.matrixSize = matrixSize > 0 ? matrixSize : 0;
        this.matrix = setupMatrix();
    }

    @Override
    public double[][] setupMatrix() {
        double[][] randomMatrix = new double[matrixSize][matrixSize];

        for(int row = 0; row < matrixSize; row++) {
            for(int col = 0; col < matrixSize; col++) {
                randomMatrix[row][col] = round(ThreadLocalRandom.current().nextDouble(MIN, MAX));
            }
        }

        return randomMatrix;
    }

    private static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
