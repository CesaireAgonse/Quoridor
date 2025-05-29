package exception;

import model.Pawn;
import model.Position;

public class PawnAlreadyHereException extends IllegalArgumentException{
    public PawnAlreadyHereException(Position position, Pawn pawn){
        super("Un pion["+pawn+"] est déja placé à la position ["+position+"] sur le plateau.");
    }
}
