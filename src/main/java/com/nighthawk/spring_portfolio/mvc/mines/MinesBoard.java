package com.nighthawk.spring_portfolio.mvc.mines;

import java.util.Random;

public class MinesBoard {
    private static final int BOARD_SIZE = 5;

    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private int mines;

    public MinesBoard(String stakes) {
        Random rng = new Random();
        mines = switch(stakes) {
            case "low" -> rng.nextInt(4, 7);
            case "medium" -> rng.nextInt(6, 9);
            case "high" -> rng.nextInt(8, 11);
            default -> -1;
        };
    }

    private void placeMines() {
        int xCoord;
        int yCoord;

        Random rng = new Random();

        for (int i = 0; i < mines; i++) {
            while (true){
                xCoord = rng.nextInt(0, BOARD_SIZE);
                yCoord = rng.nextInt(0, BOARD_SIZE);

                if (board[xCoord][yCoord] == 0) {
                    board[xCoord][yCoord] = 1;
                    break;
                }
            }
        }
    }

    private boolean checkMine(int xCoord, int yCoord) {
        return board[xCoord][yCoord] == 1;
    }

    private void printBoard() {
        for (int[] row : board) {
            for (int col : row) {
                System.out.print(col + " ");
            }
            System.out.println();
        }
    }

    // unit tester
    public static void main (String[] args) {
        MinesBoard board = new MinesBoard("high");
        board.placeMines();
        board.printBoard();
    }
}
