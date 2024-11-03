package com.nighthawk.spring_portfolio.mvc.mines;

public class Mine {
    private int x;
    private int y;
    private boolean revealed;

    public Mine(int x, int y) {
        this.x = x;
        this.y = y;
        this.revealed = false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean revealed() {
        return revealed;
    }

    public void reveal() {
        this.revealed = true;
    }

    public boolean isAtPosition(int x, int y) {
        return this.x == x && this.y == y;
    }
}
