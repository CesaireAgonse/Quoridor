package model;

enum Direction {
    HAUT(0, -1),
    BAS(0, 1),
    GAUCHE(-1, 0),
    DROITE(1, 0);

    private final int dx, dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }
}
