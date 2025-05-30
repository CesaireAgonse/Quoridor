package model;

import util.GameFactory;

import java.util.ArrayList;

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
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
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
