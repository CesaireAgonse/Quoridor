package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Game {
    private ArrayList<Player> players;
    private Board board;
    private boolean isStarted;
    private boolean isOver;

    private record score(){

    }

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

    public boolean isPlayerCanMove(int idPlayer){
        var player = players.get(idPlayer);
        var pawns = player.getPawns();

        for (var pawn : pawns){
            if (!board.isPawnCanMove(pawn)){
                return false;
            }
        }
        return true;
    }

    public boolean calculationOver(){
        if (!isStarted){
            throw new IllegalArgumentException();
        }
        var board = getBoard();
        var pawns = new ArrayList<Pawn>();

        //obtention des pions du plateau
        for (var player : players){
            for (var pawn : player.getPawns()){
                pawns.add(pawn);
            }
        }


        //Pour chaques pions sur le board on construit un ensemble de cases
        for (var currentPawn : pawns){
            var cellsFromPawn = new HashSet<Cell>();
            var pawnPosition = currentPawn.getPosition();
            var currentCell = getBoard().getCellAt(pawnPosition);

            cellsFromPawn.addAll(board.getAreaFromPosition(pawnPosition, cellsFromPawn));

            //lister tout les pions de l'ensemble
        }


        return isOver;
    }

    public boolean isGameOver() {
        return isOver;
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
