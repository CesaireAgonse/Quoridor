package util;

import model.Game;
import model.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class GameFactory {

    public static Game createGameFromInput() {
        int numberOfPlayers = askNumberOfPlayers();
        ArrayList<String> playerNames = askPlayerNames(numberOfPlayers);
        return createGame(numberOfPlayers, playerNames);
    }

    private static int askNumberOfPlayers() {
        Scanner scanner = new Scanner(System.in);
        int numberOfPlayers;
        while (true) {
            System.out.print("Entrez le nombre de joueurs (2 à 4) : ");
            numberOfPlayers = scanner.nextInt();
            scanner.nextLine(); // Consommer la nouvelle ligne
            if (numberOfPlayers >= 2 && numberOfPlayers <= 4) {
                break;
            } else {
                System.out.println("Nombre invalide. Une partie doit se joueur entre 2 et 4 joueurs.");
            }
        }
        return numberOfPlayers;
    }

    private static ArrayList<String> askPlayerNames(int numberOfPlayers) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> playerNames = new ArrayList<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            System.out.print("Entrez le nom du joueur " + i + " : ");
            String name = scanner.nextLine();
            playerNames.add(name);
        }
        return playerNames;
    }

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
            Player player = new Player(i + 1, pawnsPerPlayer, playerNames.get(i));
            players.add(player);
        }
        return new Game(players);
    }

    public static int getNumberOfPawnFromPlayer(int numberOfPlayers) {
        return switch (numberOfPlayers) {
            case 2 -> 4;
            case 3 -> 2;
            case 4 -> 1;
            default -> throw new IllegalStateException("Nombre de joueurs invalide : " + numberOfPlayers);
        };
    }

}
