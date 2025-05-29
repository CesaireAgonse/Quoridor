package model;

import java.util.Objects;

public class Position {
    private int x, y;

    public Position(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates must be non-negative.");
        }
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    /**
     * Retourne la distance entre deux positions
     * @param other la position à comparer
     * @return la distance entre les deux positions
     */
    public int distanceTo(Position other) {
        Objects.requireNonNull(other, "Other position cannot be null");
        return Math.abs(this.getX() - other.getX()) + Math.abs(this.getY() - other.getY());

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position other = (Position) obj;
            return this.x == other.x && this.y == other.y;
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}