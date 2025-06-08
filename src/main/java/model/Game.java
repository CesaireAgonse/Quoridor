package model;

import java.util.ArrayList;
import java.util.Objects;

public class Game {

    private final int NUMBER_OF_PAWNS_PER_PLAYER;
    private ArrayList<Player> players;
    private Board board;
    private boolean isStarted;
    private int currentPlayerIndex;

    public Game(ArrayList<Player> players){
        this.board = new Board();
        this.isStarted = false;
        this.players = players;
        this.NUMBER_OF_PAWNS_PER_PLAYER = players.getFirst().getPawns().length;
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

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean isAllPawnArePlaced() {
        if (board.getPawnsOnBoard() == players.size() * NUMBER_OF_PAWNS_PER_PLAYER) {
            return true; // Tous les pions sont déjà placés
        }
        return false; // Tous les pions sont placés sur le plateau
    }

    /**
     * Place le pion du joueur à la position donnée.
     * @param player
     * @param numPawn
     * @param position
     * @return true si le pion a été placé avec succès, false sinon.
     */
    public boolean playerPlacePawns(Player player, int numPawn, Position position) {;
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
