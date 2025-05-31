package model;

import util.GameFactory;

import java.util.ArrayList;
import java.util.Objects;

public class Game {
    private ArrayList<Player> players;
    private Board board;
    private boolean isStarted;

    public Game(ArrayList<Player> players){
        this.board = new Board();
        this.isStarted = false;
        this.players = players;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isStarted() {
        return isStarted;
    }

    /**
     * Place le pion du joueur à la position donnée.
     * @param player
     * @param numPawn
     * @param position
     * @return true si le pion a été placé avec succès, false sinon.
     */
    public boolean PlayerPlacePawns(Player player, int numPawn, Position position) {;
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(position, "Position cannot be null");
        if (numPawn < 0 || numPawn >= player.getPawns().length) {
            throw new IllegalArgumentException("Invalid pawn number: " + numPawn);
        }
        if (!board.isPositionOnBoard(position)) {
            return false;
        }
        return board.placePawnAt(player.getPawns()[numPawn], position);
    }

    /**
     * Démarre le jeu
     */
    public void start(){
        if (isStarted) {
            throw new IllegalStateException("Game is already started");
        }
        isStarted = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Game :\n");
        sb.append("players = ").append(players);
        sb.append(",\nboard =\n").append(board.displayBoard());
        sb.append(",\nisStarted = ").append(isStarted);
        sb.append("\n}");
        return sb.toString();
    }

    public static void main(String[] args) {
    }
}
