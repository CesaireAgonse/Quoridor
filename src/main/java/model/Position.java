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

    /**
     * Renvoie une nouvelle position déplacée dans la direction spécifiée.
     * @param direction la direction dans laquelle déplacer la position
     * @return une nouvelle position déplacée
     */
    public Position move(Direction direction) {
        Objects.requireNonNull(direction, "Direction cannot be null");
        switch (direction) {
            case NORTH:
                return new Position(this.x, this.y - 1);
            case SOUTH:
                return new Position(this.x, this.y + 1);
            case WEST:
                return new Position(this.x - 1, this.y);
            case EAST:
                return new Position(this.x + 1, this.y);
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}