package exception;

import model.Position;

public class OutOfBoardException extends IndexOutOfBoundsException{

    public OutOfBoardException(String s) {
        super(s);
    }

    public OutOfBoardException(Position position) {
        this("La position [" + position + "] n'est pas sur le plateau.");
    }
}