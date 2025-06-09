package model;

import util.GameFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

    private final int NUMBER_OF_PAWNS_PER_PLAYER;
    private ArrayList<Player> players;
    private Board board;
    private boolean isStarted;
    private int currentPlayerIndex;
    private boolean isOver = false;

    private HashMap<Player,Integer> scores;

    public Game(ArrayList<Player> players){
        this.board = new Board();
        this.players = players;
        this.NUMBER_OF_PAWNS_PER_PLAYER = players.getFirst().getPawns().length;
        this.scores = new HashMap<>();
        this.scores.putAll(players.stream().collect(Collectors.toMap(player -> player, player -> 0)));
        this.currentPlayerIndex = 0;
        this.isStarted = false;
    }

    public void reset(){
        var listNames = new ArrayList<String>();
        listNames.addAll(players.stream().map(Player::getName).collect(Collectors.toList()));
        var newGame = GameFactory.createGame(players.size(), listNames);
        this.board = newGame.getBoard();
        this.players = newGame.getPlayers();
        this.scores = newGame.getScores();
        this.scores.putAll(players.stream().collect(Collectors.toMap(player -> player, player -> 0)));
        this.currentPlayerIndex = newGame.currentPlayerIndex;
        this.isStarted = newGame.isStarted;
        this.isOver = newGame.isOver;
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
        if (isOver) {
            throw new IllegalStateException("Game is over, cannot proceed to next player");
        }
        if (isStarted) {
            calculationOver();
        }
        var nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (isStarted){
            while (!isPlayerCanPlay(nextPlayerIndex)){
               nextPlayerIndex = processNextPlayer(nextPlayerIndex);
            }
        }
        currentPlayerIndex = nextPlayerIndex;
    }

    private int processNextPlayer(int indexPlayer) {
        return (indexPlayer + 1) % players.size();
    }

    public Player getPlayerById(int id) {
        if (id < 0 || id >= players.size()) {
            throw new IllegalArgumentException("Invalid player ID: " + id);
        }
        return players.get(id);
    }

    public boolean isAllPawnArePlaced() {
        if (board.getPawnsOnBoard() == players.size() * NUMBER_OF_PAWNS_PER_PLAYER) {
            return true; // Tous les pions sont déjà placés
        }
        return false; // Tous les pions sont placés sur le plateau
    }

    /**
     * Vérifie si un joueur peut bouger au moins un de ses pions.
     * @param idPlayer l'identifiant du joueur à vérifier
     * @return true si le joueur peut bouger au moins un de ses pions, false sinon.
     */
    public boolean isPlayerCanMove(int idPlayer){
        var player = getPlayerById(idPlayer);
        var pawns = player.getPawns();

        for (var pawn : pawns){
            if (!board.isPawnCanMove(pawn)){
                return false;
            }
        }
        return true;
    }

    /**
     * Vérifie si un joueur peut joueur au moins un de ses pions. C'est-à-dire s'il peut placer un mur.
     * @param idPlayer l'identifiant du joueur à vérifier
     * @return true si le joueur peut bouger au moins un de ses pions, false sinon.
     */
    public boolean isPlayerCanPlay(int idPlayer){
        var player = getPlayerById(idPlayer);
        var pawns = player.getPawns();
        var pawnCannotMove = 0;

        for (var pawn : pawns){
            if (!board.isPawnCanPlaceWall(pawn)){
                pawnCannotMove++;
            }
        }
        return pawnCannotMove < pawns.length;
    }

    public void calculationOver(){
        if (!isStarted){
            throw new IllegalStateException("Game has not started yet");
        }

        var processedCells = new HashSet<Cell>(); // Pour éviter les doublons
        var playerScores = new HashMap<Player, Integer>(); // Map des scores

        // Initialiser les scores à 0
        playerScores.putAll(players.stream().collect(Collectors.toMap(player -> player, player -> 0)));

        // Obtention de tous les pions du plateau
        var allPawns = new ArrayList<Pawn>();
        //obtention des pions du plateau
        for (var player : players){
            for (var pawn : player.getPawns()){
                allPawns.add(pawn);
            }
        }

        var zonesControlledBySinglePlayer = 0; // Compteur pour déterminer la fin de partie
        var totalZones = 0;

        // Pour chaque pion sur le board, calculer sa zone
        for (var currentPawn : allPawns){
            var pawnPosition = currentPawn.getPosition();
            var currentCell = board.getCellAt(pawnPosition);

            // Éviter de recalculer une zone déjà traitée
            if (processedCells.contains(currentCell)) {
                continue;
            }

            // Obtenir toutes les cellules de la zone connectée
            var cellsFromPawn = board.getAreaFromPosition(pawnPosition);

            // Obtenir tous les pions dans cette zone groupés par joueur
            var pawnsByPlayer = getPawnsFromCells(cellsFromPawn);

            totalZones++;

            // Vérifier si la zone appartient à un seul joueur
            if (pawnsByPlayer.size() == 1) {
                // Zone contrôlée par un seul joueur
                var controllingPlayer = pawnsByPlayer.keySet().iterator().next();
                var scoreToAdd = cellsFromPawn.size();

                // Ajouter les points au joueur dans la map
                playerScores.put(controllingPlayer, playerScores.get(controllingPlayer) + scoreToAdd);

                zonesControlledBySinglePlayer++;
            }

            // Marquer toutes les cellules de cette zone comme traitées
            processedCells.addAll(cellsFromPawn);
        }

        this.scores = playerScores;

        // La partie est terminée si toutes les zones sont contrôlées par un seul joueur chacune
        isOver = (totalZones > 0 && zonesControlledBySinglePlayer == totalZones);
    }

    /**
     * Trouve le joueur propriétaire d'un pion
     */
    private Player findPlayerByPawn(Pawn pawn) {
        if (pawn.getPlayerId() > players.size() || pawn.getPlayerId() < 0) {
            throw new IllegalArgumentException("Invalid player ID: " + pawn.getPlayerId());
        }
        return players.get(pawn.getPlayerId());
    }

    public HashMap<Player, Integer> getScores() {
        return scores;
    }

    /**
     * Crée une hashmap de tous les pions d'un ensemble de cellules
     * @return une hashmap de joueurs et de leurs pions
     */
    private HashMap<Player, ArrayList<Pawn>> getPawnsFromCells(Set<Cell> cells){
        HashMap<Player, ArrayList<Pawn>> pawns = new HashMap<>();
        for (var cell : cells){
            if (cell.isOccuped()){
                var pawn = cell.getOptionalPawn().get();
                var playerid = pawn.getPlayerId();
                var player = players.get(playerid);
                pawns.putIfAbsent(player, new ArrayList<>());
                pawns.get(player).add(pawn);
            }
        }
        return pawns;
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
