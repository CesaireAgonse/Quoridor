package model;

import java.util.ArrayList;

public class Game {
    private ArrayList<Player> players;
    private Board board;
    private boolean isStarted;

    public Game(ArrayList<Player> players){
        this.players = players;
        this.board = new Board(7);
        this.isStarted = false;
    }
}
