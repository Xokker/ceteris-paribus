package com.xokker;

import org.apache.commons.math3.linear.RealMatrix;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * @author Ernest Sadykov
 * @since 21.04.2015
 */
public class MatrixPrinter {

    public static void println(RealMatrix matrix) {
        println(matrix, System.out);
    }

    public static void println(RealMatrix matrix, PrintStream printStream) {
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            printStream.println(Arrays.toString(matrix.getRow(row)));
        }
        printStream.println();
    }

}
