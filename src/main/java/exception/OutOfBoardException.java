package exception;

import model.Position;

public class OutOfBoardException extends IllegalArgumentException{

    public OutOfBoardException(Position position){
        super("La position ["+position+"] n'est pas sur le plateau.");
    }
}
