import model.Game;
import model.Pawn;
import model.Player;
import model.Position;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("yo !");
        var j1 = new Player(1, "Bob");
        var j2 = new Player(2, "Marley");
        //var p1 = new Pawn(1, j1.getId(), new Position(5,4));

        System.out.println(j1);
        System.out.println(j2);

        var players = new ArrayList<Player>();
        players.add(j1);
        players.add(j2);

        var game = new Game(players);
    }
}
