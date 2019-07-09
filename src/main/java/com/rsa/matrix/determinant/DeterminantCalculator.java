package com.rsa.matrix.determinant;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;

public class DeterminantCalculator {
    public static void main(String[] args) {
        // initialize defaults
        long executionStart = System.currentTimeMillis();
        Matrix matrix = null;
        BigDecimal determinant;

        File outputFile = null;
        int threadsCount = 1;
        boolean isQuiet = false;

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        options.addOption("t", "tasks", true, "Threads count");
        options.addOption("n", true, "Matrix size");
        options.addOption("i", "input", true, "Input matrix from file");
        options.addOption("o", "output", true, "Save result in file");
        options.addOption("q", "quiet", false, "Quiet mode");

        CommandLine commands = null;
        try {
            commands = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("Problem with parsing options.");
            e.printStackTrace();
        }

        if(commands.hasOption("t") && commands.getOptionValue("t") != null) {
            // TODO validation
            threadsCount = Integer.parseInt(commands.getOptionValue("t"));
        }

        if(commands.hasOption("o") && commands.getOptionValue("o") != null) {
            outputFile = new File(commands.getOptionValue("o"));
        }

        if(commands.hasOption("q")) {
            isQuiet = true;
        }

        if(commands.hasOption("n") && commands.hasOption("i")) {
            System.out.println("You either generate random matrix or read it from file! Cannot do both.");
            return;
        }

        if(commands.hasOption("n") && commands.getOptionValue("n") != null) {
            int matrixSize = Integer.parseInt(commands.getOptionValue("n"));
            matrix = new RandomMatrix(matrixSize, isQuiet);

            System.out.println("Matrix generated successfully:");
//            ((RandomMatrix) matrix).printMatrix();
        }

        if(commands.hasOption("i") && commands.getOptionValue("i") != null) {
            String filename = commands.getOptionValue("i");
            File inputFile = new File(filename);

            try {
                matrix = new FileMatrix(isQuiet, inputFile);
            } catch (FileNotFoundException e) {
                System.err.println("No such file: " + filename);
                e.printStackTrace();
            }
        }

        if(matrix == null) {
            System.err.println("Problem with initializing matrix!");
            return;
        }

        determinant = matrix.getDeterminant(threadsCount);

        if(outputFile != null) {
            matrix.saveResult(outputFile);
        }

        long executionEnd = System.currentTimeMillis();
        long executionTime = executionEnd - executionStart;

        System.out.println("Total execution time for current run (millis): " + executionTime);
        System.out.printf("detA = %.3E%n", determinant);
    }
}
