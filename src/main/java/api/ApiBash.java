package api;

import model.Game;
import model.Player;
import model.Position;
import util.GameFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApiBash implements Api {
    /**
     * Initialisation du jeu en demandant le nombre de joueurs et leurs noms.
     */
    @Override
    public Game createGame() {
        int numberOfPlayers = askNumberOfPlayers();
        ArrayList<String> playerNames = askPlayerNames(numberOfPlayers);
        return GameFactory.createGame(numberOfPlayers, playerNames);
    }

    /**
     * Phase de placement des pions pour chaque joueur.
     * Chaque joueur place ses pions un par un, en vérifiant que la position est valide.
     * @param game le jeu en cours modifié avec les pions placés.
     */
    @Override
    public Game placementPawns(Game game) {
        List<Player> players = game.getPlayers();
        int totalPawns = players.get(0).getPawns().length;

        for (int i = 0; i < totalPawns; i++) {
            for (Player player : players) {
                System.out.println(game.getBoard().displayBoard()); // Affiche le plateau avant chaque placement
                boolean placed = false;

                System.out.println("Tour du joueur : " + player.getName());
                System.out.println("Placement du pion #" + (i + 1));

                while (!placed) {
                    Position pos = askPosition(); // méthode existante
                    placed = game.PlayerPlacePawns(player, i, pos);

                    if (!placed) {
                        System.out.println("Position invalide ou déjà occupée. Réessayez.");
                    }
                }
            }
        }
        return game;
    }

    /**
     * Demande le nombre de joueurs pour la partie.
     * @return le nombre de joueurs, qui doit être compris entre 2 et 4.
     */
    @Override
    public int askNumberOfPlayers() {
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

    /**
     * Demande les noms des joueurs.
     * @param numberOfPlayers le nombre de joueurs pour la partie.
     * @return une liste de noms de joueurs
     */
    @Override
    public ArrayList<String> askPlayerNames(int numberOfPlayers) {
        if (numberOfPlayers < 2 || numberOfPlayers > 4) {
            throw new IllegalArgumentException("Le nombre de joueurs doit être compris entre 2 et 4.");
        }
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> playerNames = new ArrayList<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            System.out.print("Entrez le nom du joueur " + i + " : ");
            String name = scanner.nextLine();
            playerNames.add(name);
        }
        return playerNames;
    }

    /**
     * Demande une position pour placer un pion.
     * @return la position demandée sous forme d'objet Position.
     */
    @Override
    public Position askPosition() {
        Scanner scanner = new Scanner(System.in);
        int x, y;
        System.out.print("Entrez la position (x, y)\n");
        while (true) {
            System.out.print("Entrez la coordonnée X : ");
            try {
                x = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("X doit être un entier.");
                scanner.nextLine(); // Consommer la ligne invalide
                continue;
            }
            if (x < 1) {
                System.out.println("X doit être positif.");
                continue;
            }
            break;
        }
        while (true) {
            System.out.print("Entrez la coordonnée Y : ");
            try {
                y = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Y doit être un entier.");
                scanner.nextLine(); // Consommer la ligne invalide
                continue;
            }
            if (y < 1) {
                System.out.println("Y doit être positif.");
                continue;
            }
            break;
        }
        return new Position(x - 1, y - 1); // +1 pour correspondre à la logique de positionnement du jeu
    }
}
