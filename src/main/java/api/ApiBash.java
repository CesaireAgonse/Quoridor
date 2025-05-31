package api;

import exception.OutOfBoardException;
import model.*;
import util.GameFactory;

import java.util.*;

public class ApiBash implements Api {

    private Scanner scanner;

    public ApiBash() {
        this.scanner = new Scanner(System.in);
    }
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
     * Joue une partie.
     * @param game le jeu à jouer.
     */
    @Override
    public void playGame(Game game) {
        Objects.requireNonNull(game, "Game cannot be null");
        if (game.isStarted()) {
            throw new IllegalStateException("Game is already started");
        }
        game.start();

        //chaque joueur joue son tour chacun à son tour
        List<Player> players = game.getPlayers();
        int currentPlayerIndex = 0;
        while (true) {
            System.out.println(game.getBoard().displayBoard());

            Player currentPlayer = players.get(currentPlayerIndex);
            System.out.println("C'est le tour du joueur : " + currentPlayer.getName());
            var capacityUsedThisTurn = false;

            if (players.get(currentPlayerIndex).isCapacityUsed()) {
                System.out.println("Vous avez déjà utilisé votre capacité spéciale.");
            } else if (askUseCapacity()) {
                capacityUsedThisTurn = true;
            }

            Pawn pawnFromPosition = null;
            boolean validPawnSelected = false;
            boolean wallplaced = false;

            //Demander au joueur de sélectionner un pion à lui en indiquant sa position sur la grille
            System.out.println("Sélectionnez un pion à déplacer en indiquant sa position : ");
            while (!validPawnSelected) {
                Position position = askPosition();
                var cell = game.getBoard().getCellAt(position);
                if (!cell.isOccuped()) {
                    System.out.println("Aucun pion à cette position. Réessayez.");
                    continue; // Redemander la position si aucun pion n'est trouvé
                }
                if (cell.getOptionalPawn().get().getPlayerId() != currentPlayer.getId()) {
                    System.out.println("Ce n'est pas votre pion. Réessayez.");
                    continue; // Redemander la position si le pion n'appartient pas au joueur
                }

                pawnFromPosition = cell.getOptionalPawn().get();
                validPawnSelected = true; // Si on arrive ici, le pion est valide
            }

            // Demander au joueur s'il veut se déplacer de 0, 1 ou 2 cases
            int numberOfMoves = askNumberOfMoves();

            // Demander au joueur de sélectionner une direction pour déplacer le pion
            var tmpPosition = pawnFromPosition.getPosition();
            for (int i = 0; i < numberOfMoves; i++) {
                boolean moved = false;
                System.out.println("Sélectionnez une direction pour déplacer le pion : ");
                Position newPosition;
                while(!moved){
                    Direction direction = askDirection();
                    try {
                        moved = game.getBoard().movePawnAt(tmpPosition, direction);

                        /**
                        //colision avec un mur dans le cas ou la capacité spéciale est utilisée
                        if (capacityUsedThisTurn && !currentPlayer.isCapacityUsed()
                                && !moved
                                && game.getBoard().isPositionOnBoard(tmpPosition.move(direction))) {
                            System.out.println("Capacité spéciale utilisée !");
                            currentPlayer.useCapacity();
                            game.getBoard().removeWall(tmpPosition, direction);
                            System.out.println(game.getBoard().displayBoard());
                            continue;
                        }
                         */

                    } catch (OutOfBoardException e) {
                        System.out.println("Déplacement impossible, le pion sort du plateau. Réessayez.");
                        continue;
                    }
                    if (moved) {
                        newPosition = tmpPosition.move(direction);
                        tmpPosition = newPosition;
                    }
                }

                System.out.println(game.getBoard().displayBoard());
            }

            // Demander au joueur de sélectionner une direction pour placer un mur
            System.out.println("Sélectionnez une direction pour placer un mur : ");
            while (!wallplaced){
                Direction direction = askDirection();
                wallplaced = game.getBoard().placeWall(pawnFromPosition.getPosition(), direction);
            }

            // TODO Check condition de victoire

            // Passer au joueur suivant
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

    }

    /**
     * Demande le nombre de joueurs pour la partie.
     * @return le nombre de joueurs, qui doit être compris entre 2 et 4.
     */
    @Override
    public int askNumberOfPlayers() {
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

    /**
     * Demande une direction dans le terminal
     * @return la direction demandée sous forme d'objet Direction.
     */
    @Override
    public Direction askDirection() {
        Direction direction = null;
        while (direction == null) {
            System.out.print("Entrez la direction (Z ↑, D →, S ↓, Q ←) : ");
            String input = scanner.nextLine().trim().toUpperCase();
            switch (input) {
                case "Z":
                    direction = Direction.NORTH;
                    break;
                case "S":
                    direction = Direction.SOUTH;
                    break;
                case "D":
                    direction = Direction.EAST;
                    break;
                case "Q":
                    direction = Direction.WEST;
                    break;
                default:
                    System.out.println("Direction invalide. Veuillez entrer N, S, E ou W.");
            }
        }
        return direction;
    }

    /**
     * Demande le nombre de déplacements pour un pion.
     * @return le nombre de déplacements, qui doit être compris entre 0 et 2.
     */
    @Override
    public int askNumberOfMoves() {
        int numberOfDeplacement;
        while (true) {
            System.out.print("Entrez le nombre de déplacements (0, 1 ou 2) : ");
            try {
                numberOfDeplacement = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Le nombre de déplacements doit être un entier.");
                scanner.nextLine(); // Consommer la ligne invalide
                continue;
            }
            scanner.nextLine(); // Consommer la nouvelle ligne
            if (numberOfDeplacement >= 0 && numberOfDeplacement <= 2) {
                break;
            } else {
                System.out.println("Nombre invalide. Vous devez entrer 0, 1 ou 2.");
            }
        }
        return numberOfDeplacement;
    }

    /**
     * Demande si le joueur souhaite utiliser sa capacité spéciale.
     * @return true si le joueur souhaite utiliser sa capacité spéciale, false sinon.
     */
    @Override
    public boolean askUseCapacity() {
        System.out.print("Voulez-vous utiliser la capacité spéciale de votre joueur ? (O/N) : ");
        String response = scanner.nextLine().trim().toUpperCase();
        return response.equals("O");
    }
}
