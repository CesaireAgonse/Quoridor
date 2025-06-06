package util;

import model.Game;
import model.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class GameFactory {
    public static Game createGame(int numberOfPlayers, ArrayList<String> playerNames) {
        int pawnsPerPlayer = getNumberOfPawnFromPlayer(numberOfPlayers);
        return createGame(numberOfPlayers, playerNames, pawnsPerPlayer);
    }

    public static Game createGame(int numberOfPlayers, ArrayList<String> playerNames, int pawnsPerPlayer) {
        if (numberOfPlayers < 2 || numberOfPlayers > 4) {
            throw new IllegalArgumentException("Le nombre de joueurs doit être entre 2 et 4.");
        }

        if (playerNames.size() != numberOfPlayers) {
            throw new IllegalArgumentException("Le nombre de noms de joueurs doit correspondre au nombre de joueurs.");
        }

        ArrayList<Player> players = new ArrayList<>();

        for (int i = 0; i < numberOfPlayers; i++) {
            Player player = new Player(i , pawnsPerPlayer, playerNames.get(i));
            players.add(player);
        }
        return new Game(players);
    }

    public static int getNumberOfPawnFromPlayer(int numberOfPlayers) {
        return switch (numberOfPlayers) {
            case 2 -> 4;
            case 3 , 4 -> 2;
            default -> throw new IllegalStateException("Nombre de joueurs invalide : " + numberOfPlayers);
        };
    }
}
