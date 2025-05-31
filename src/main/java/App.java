import api.Api;
import api.ApiBash;
import model.Game;
import model.Player;
import model.Position;
import util.GameFactory;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        // Création de l'API
        Api api = new ApiBash();
        var mock = true; // Pour activer le mock, mettre à true
        Game game;

        if (mock) {
            //Phase d'initialisation du jeu
            var playersNames = new ArrayList<String>();
            playersNames.add("Alice");
            playersNames.add("Bob");
            game = GameFactory.createGame(2, playersNames);

            //Phase de placement des pions
            game.getBoard().placePawnAt(game.getPlayers().get(0).getPawns()[0], new Position(0, 0));
            game.getBoard().placePawnAt(game.getPlayers().get(0).getPawns()[1], new Position(6, 1));
            game.getBoard().placePawnAt(game.getPlayers().get(0).getPawns()[2], new Position(4, 2));
            game.getBoard().placePawnAt(game.getPlayers().get(0).getPawns()[3], new Position(4, 5));
            game.getBoard().placePawnAt(game.getPlayers().get(1).getPawns()[0], new Position(1, 1));
            game.getBoard().placePawnAt(game.getPlayers().get(1).getPawns()[1], new Position(5, 3));
            game.getBoard().placePawnAt(game.getPlayers().get(1).getPawns()[2], new Position(2, 5));
            game.getBoard().placePawnAt(game.getPlayers().get(1).getPawns()[3], new Position(0, 6));

            System.out.println("Phase de jeu !");
            api.playGame(game);
        } else {
            //Phase d'initialisation du jeu
            System.out.println("Phase d'initialisation :");
            game = api.createGame();
            System.out.println(game);

            //Phase de placement des pions
            System.out.println("Phase de placement des pions :");
            game = api.placementPawns(game);
        }

        //Phase de jeu
        System.out.println("Phase de jeu !");
        api.playGame(game);
    }
}
