package com.rsa.matrix.determinant;

import java.io.*;
import java.util.Scanner;

public class FileMatrix extends BaseMatrix {
    private File inputFile;

    public FileMatrix(boolean isQuiet, File inputFile) throws FileNotFoundException {
        super(isQuiet);

        if(inputFile.exists() && inputFile.isFile()) {
            this.inputFile = inputFile;
            this.matrix = setupMatrix();
        }
        else throw new FileNotFoundException("No such file.");

    }

    @Override
    public double[][] setupMatrix() {
        double[][] matrix = null;
        Scanner scan;
        try {
            scan = new Scanner(inputFile);
            this.matrixSize = Integer.parseInt(scan.nextLine());
            matrix = new double[this.matrixSize][this.matrixSize];

            for(int row = 0; row < this.matrixSize; row++) {
                for(int col = 0; col < this.matrixSize; col++) {
                    matrix[row][col] = scan.nextInt();
                }
            }
            scan.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }

        return matrix;
    }

}
