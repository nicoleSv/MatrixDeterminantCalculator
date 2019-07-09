package com.rsa.matrix.determinant;

import java.io.File;
import java.math.BigDecimal;

public interface Matrix {
    double[][] setupMatrix();

    // could return BigDouble
    BigDecimal getDeterminant(int threadsCount);

    void saveResult(File resultFile);
}
