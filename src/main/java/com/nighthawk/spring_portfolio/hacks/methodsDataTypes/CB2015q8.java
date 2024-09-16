package com.nighthawk.spring_portfolio.hacks.methodsDataTypes;

import java.util.Arrays;

public class CB2015q8 {
    /**
     * Performs a matrix operation on the given matrix, row, and column.
     * The result is stored in an array and returned.
     *
     * @param matrix the matrix to perform the operation on
     * @param r the row index
     * @param c the column index
     * @return an array containing the result of the operation
     */
    public static int[] operation(int[][] matrix, int r, int c) {
        int[] result = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            /* 
            i is the loop variable that iterates from 0 to the length of the matrix
            r is the given row index
            c is the given column index
            result[i] is the element at index i in the result array
            matrix[r][i] is the element at row r and column i in the matrix
            matrix[i][c] is the element at row i and column c in the matrix
            The result at index i is calculated by multiplying the corresponding elements
            result[0] = [1][0] * [0][2]
            result[1] = [1][1] * [1][2]
            result[2] = [1][2] * [2][2]
            */
            result[i] = matrix[r][i] * matrix[i][c];
        }
        return result;
    }

    // Test data from College Board
    public static void main(String[] args) {
        int[][] mat = {
            {3, 2, 1, 4},
            {1, 2, 3, 4},
            {2, 2, 1, 2},
            {1, 1, 1, 1},
        };
        int[] arr = operation(mat, 1, 2);
        System.out.println(Arrays.deepToString(mat)); // print 2D matrix
        System.out.println(Arrays.toString(arr)); // print 1D matrix
    }
}
