package model;

public class Player {
    private int id;
    private String nom;
    private Pawn[] Pawns;
    private boolean capaciteUtilisee;

    public Player(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.Pawns = new Pawn[2];
        this.capaciteUtilisee = false;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public Pawn[] getPawns() { return Pawns; }
    public boolean isCapaciteUtilisee() { return capaciteUtilisee; }
    public void utiliserCapacite() { this.capaciteUtilisee = true; }

    public void placerPawn(int index, Position position) {
        if (index >= 0 && index < 2) {
            Pawns[index] = new Pawn(id, index, position);
        }
    }

    public Pawn getPawn(int index) {
        return (index >= 0 && index < 2) ? Pawns[index] : null;
    }

    @Override
    public String toString() {
        return "Joueur " + id + " (" + nom + ") - Capacité utilisée: " + capaciteUtilisee;
    }
}
