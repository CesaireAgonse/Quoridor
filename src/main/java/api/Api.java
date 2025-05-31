package api;

import model.Direction;
import model.Game;
import model.Position;

import java.util.ArrayList;

public interface Api {

    /**
     * Initialisation du jeu en demandant le nombre de joueurs et leurs noms.
     */
    Game createGame();

    /**
     * Phase de placement des pions pour chaque joueur.
     * Chaque joueur place ses pions un par un, en vérifiant que la position est valide.
     * @param game le jeu en cours modifié avec les pions placés.
     */
    Game placementPawns(Game game);

    /**
     * Joue une partie.
     * @param game le jeu à jouer.
     */
    void playGame(Game game);

    /**
     * Demande le nombre de joueurs pour la partie.
     * @return le nombre de joueurs, qui doit être compris entre 2 et 4.
     */
    int askNumberOfPlayers();

    /**
     * Demande les noms des joueurs.
     * @param numberOfPlayers le nombre de joueurs pour la partie.
     * @return une liste de noms de joueurs
     */
    ArrayList<String> askPlayerNames(int numberOfPlayers);

    /**
     * Demande une position pour placer un pion.
     * @return la position demandée sous forme d'objet Position.
     */
    Position askPosition();

    /**
     * Demande une direction
     * @return la direction demandée sous forme d'objet Direction.
     */
    Direction askDirection();

    /**
     * Demande le nombre de déplacements pour un pion.
     * @return le nombre de déplacements, qui doit être compris entre 0 et 2.
     */
    int askNumberOfMoves();

}
