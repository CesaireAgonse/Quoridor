package model;

import java.util.Objects;

public class Pawn {
    private int id;
    private Position position;
    private final int playerId;

    private String displayName = "X";

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

    public void setDisplayName(String displayName) {
        Objects.requireNonNull(displayName, "Display name cannot be null");
        this.displayName = displayName;
    }
    public String display(){
        return displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pawn) {
            Pawn other = (Pawn) obj;
            return this.id == other.id && this.playerId == other.playerId && this.position.equals(other.position);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Pion " + id + " - du joueur " + playerId + " " + position;
    }
}
