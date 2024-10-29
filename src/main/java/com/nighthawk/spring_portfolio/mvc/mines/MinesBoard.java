package com.nighthawk.spring_portfolio.mvc.mines;

import java.util.Random;

public class MinesBoard {
    private static final int BOARD_SIZE = 5;

    private static final double LOW_INITIAL = 0.7;
    private static final double MEDIUM_INITIAL = 0.6;
    private static final double HIGH_INITIAL = 0.5;
    private static final double LOW_MULTIPLIER = 1.1;
    private static final double MEDIUM_MULTIPLIER = 1.125;
    private static final double HIGH_MULTIPLIER = 1.15;

    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private String stakes;
    private int mines;
    private int cleared = -1;

    public MinesBoard(String stakes) {
        Random rng = new Random();
        this.stakes = stakes;

        mines = switch (stakes) {
            case "low" -> rng.nextInt(4, 7);
            case "medium" -> rng.nextInt(6, 9);
            case "high" -> rng.nextInt(8, 11);
            default -> -1;
        };

        placeMines();
    }

    private void placeMines() {
        int xCoord;
        int yCoord;

        Random rng = new Random();

        for (int i = 0; i < mines; i++) {
            while (true) {
                xCoord = rng.nextInt(0, BOARD_SIZE);
                yCoord = rng.nextInt(0, BOARD_SIZE);

                if (board[xCoord][yCoord] == 0) {
                    board[xCoord][yCoord] = 1;
                    break;
                }
            }
        }
    }

    public boolean checkMine(int xCoord, int yCoord) {
        cleared++;
        return board[xCoord][yCoord] == 1;
    }

    public double winnings() {
         double pts = switch (stakes) {
             case "low" -> LOW_INITIAL * Math.pow(LOW_MULTIPLIER, cleared);
             case "medium" -> MEDIUM_INITIAL * Math.pow(MEDIUM_MULTIPLIER, cleared);
             case "high" -> HIGH_INITIAL * Math.pow(HIGH_MULTIPLIER, cleared);
             default -> -1;
         };

        return pts;
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
    public static void main(String[] args) {
        MinesBoard board = new MinesBoard("high");
        board.placeMines();
        board.printBoard();
    }
}
