package model;

import java.util.Objects;

public class Player {
    private int id;
    private String name;
    private Pawn[] Pawns;
    private boolean isCapacityUsed;

    public Player(int id, int numPawns, String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        if (numPawns < 1) {
            throw new IllegalArgumentException("Number of pawns must be at least 1");
        }
        this.id = id;
        this.isCapacityUsed = false;
        this.Pawns = new Pawn[numPawns];
        this.name = name;
        initPawns();
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public Pawn[] getPawns() { return Pawns; }

    public void initPawns() {
        for (int i = 0; i < Pawns.length; i++) {
            Pawns[i] = new Pawn(i, id, new Position(0,0));
            Pawns[i].setDisplayName(name.toString().substring(0, 1).toUpperCase());
        }
    }

    public boolean isCapacityUsed() { return isCapacityUsed; }
    public void useCapacity() { this.isCapacityUsed = true; }

    @Override
    public String toString() {
        return "Joueur " + id + " (" + name + ") - Capacité utilisée: " + isCapacityUsed;
    }
}
