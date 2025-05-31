import api.Api;
import api.ApiBash;
import model.Player;
import util.GameFactory;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        // Création de l'API
        Api api = new ApiBash();

        //Phase d'initialisation du jeu
        System.out.println("Phase d'initialisation :");
        var game = api.createGame();
        System.out.println(game);

        /**
        var playersNames = new ArrayList<String>();
        playersNames.add("Alice");
        playersNames.add("Bob");
        var game = GameFactory.createGame(2, playersNames);
        */

        //Phase de placement des pions
        System.out.println("Phase de placement des pions :");
        game = api.placementPawns(game);

        //Phase de jeu
        System.out.println("Phase de jeu !");
        game.start();

    }
}
