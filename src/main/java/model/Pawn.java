package model;

import java.util.Objects;

public class Pawn {
    private int id;
    private Position position;
    private final int playerId;

    public Pawn(int id, int playerId, Position position) {
        Objects.requireNonNull(position, "Position cannot be null");
        this.id = id;
        this.playerId = playerId;
        this.position = position;
    }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    public int getPlayerId() { return playerId; }
    public int getId() { return id; }

    public String display(){
        return "X";
    }
    @Override
    public String toString() {
        return "Pion " + id + " - du joueur " + playerId + " " + position;
    }
}
