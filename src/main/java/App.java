import model.Game;
import model.Player;
import util.GameFactory;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        System.out.println("Phase d'initialisation :");
        //Phase d'initialisation du jeu
        var game = GameFactory.createGameFromInput();
        System.out.println(game);

        //Phase de placement des pions
        System.out.println("Phase de placement des pions :");

        //Phase de jeu
        System.out.println("Phase de jeu !");
    }
}
