package model;

import exception.OutOfBoardException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class BoardTest {

    @Test
    void checkIfBoardHasBeenInitializedCorrectlyByDefault() {
        Board board = new Board();

        Assertions.assertEquals(7, board.SIZE, "Board size should be 7x7 by default");

        // check s'il y a mur dans tous les contours du plateau
        for (int i = 0; i < board.SIZE; i++) {
            Assertions.assertTrue(board.isWallAt(new Position(i, 0), Direction.NORTH),
                    "Cell at (" + i + ", 0) should be a horizontal wall");
            Assertions.assertTrue(board.isWallAt(new Position(i, board.SIZE), Direction.NORTH),
                    "Cell at (" + i + ", " + board.SIZE + ") should be a horizontal wall");

            Assertions.assertTrue(board.isWallAt(new Position(0 ,i), Direction.WEST),
                    "Cell at (0, " + i + ") should be a vertical wall");
            Assertions.assertTrue(board.isWallAt(new Position(board.SIZE, i), Direction.WEST),
                    "Cell at (" + board.SIZE + ", " + i + ") should be a vertical wall");
        }

        // check s'il n'y a pas de mur dans les autres cases
        for (int i = 1; i < board.SIZE; i++) {
            for (int j = 1; j < board.SIZE; j++) {
                Assertions.assertFalse(board.isWallAt(new Position(i, j), Direction.NORTH),
                        "Cell at (" + i + ", " + j + ") should not be a horizontal wall");
                Assertions.assertFalse(board.isWallAt(new Position(i, j), Direction.WEST),
                        "Cell at (" + i + ", " + j + ") should not be a vertical wall");
            }
        }

        // check si il y a pas de pion dans les cases
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                Assertions.assertFalse(board.getCellAt(new Position(i, j)).getOptionalPawn().isPresent(),
                        "Cell at (" + i + ", " + j + ") should not contain a pawn");
            }
        }


    }

    @Test
    void checkIfBoardHasBeenInitializedCorrectlyWithCustomSize() {
        var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Board board = new Board(6);
        }, "Board with a size smaller than 7 should throw an exception");

        String expectedMessage = "La taille du plateau doit être supérieure ou égale à 7.";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkIfWallCanBePlaced() {
        Board board = new Board();

        // Test placing a horizontal wall
        Position horizontalWallPosition = new Position(1, 1);
        Assertions.assertTrue(board.placeWall(horizontalWallPosition, Direction.NORTH),
                "Horizontal wall should be placed at " + horizontalWallPosition);
        Assertions.assertTrue(board.isWallAt(horizontalWallPosition, Direction.NORTH),
                "Horizontal wall should be placed at " + horizontalWallPosition);

        // Test placing a vertical wall
        Position verticalWallPosition = new Position(2, 2);
        Assertions.assertTrue(board.placeWall(verticalWallPosition, Direction.WEST),
                "Vertical wall should be placed at " + verticalWallPosition);
        Assertions.assertTrue(board.isWallAt(verticalWallPosition, Direction.WEST),
                "Vertical wall should be placed at " + verticalWallPosition);
    }

    @Test
    void checkIfWallCannotBePlacedOnAnotherWall() {
        Board board = new Board();

        // Test placing a horizontal wall
        Position horizontalWallPosition = new Position(1, 1);
        Assertions.assertTrue(board.placeWall(horizontalWallPosition, Direction.SOUTH),
                "Horizontal wall should be placed at " + horizontalWallPosition);

        // Test placing a horizontal wall at the same position
        Assertions.assertFalse(board.placeWall(horizontalWallPosition, Direction.SOUTH),
                "Horizontal wall shouldn't be placed at " + horizontalWallPosition);

        // Test placing a vertical wall
        Position verticalWallPosition = new Position(2, 2);
        Assertions.assertTrue(board.placeWall(verticalWallPosition, Direction.WEST),
                "Vertical wall should be placed at " + verticalWallPosition);

        // Test placing a vertical wall at the same position
        Assertions.assertFalse(board.placeWall(verticalWallPosition, Direction.WEST),
                "Vertical wall shouldn't be placed at " + verticalWallPosition);
    }

    @Test
    void checkIfWallCannotBePlacedOutsideOfBoard(){
        Board board = new Board();

        // Test placing a horizontal wall outside the board
        Position horizontalWallPosition = new Position(board.SIZE, 1);
        Assertions.assertFalse(board.placeWall(horizontalWallPosition, Direction.SOUTH),
                "Horizontal wall shouldn't be placed outside the board at " + horizontalWallPosition);

        // Test placing a vertical wall outside the board
        Position verticalWallPosition = new Position(2, board.SIZE);
        Assertions.assertFalse(board.placeWall(verticalWallPosition, Direction.EAST),
                "Vertical wall shouldn't be placed outside the board at " + verticalWallPosition);
    }

    @Test
    void checkIfWallCanBeRemoved(){
        Board board = new Board();

        // Test placing a horizontal wall
        Position horizontalWallPosition = new Position(1, 1);
        board.placeWall(horizontalWallPosition, Direction.NORTH);
        board.removeWall(horizontalWallPosition, Direction.NORTH);
        Assertions.assertFalse(board.isWallAt(horizontalWallPosition, Direction.NORTH),
                "Horizontal wall shouldn't be placed at " + horizontalWallPosition);

        // Test placing a vertical wall
        Position verticalWallPosition = new Position(2, 2);
        board.placeWall(verticalWallPosition, Direction.WEST);
        board.removeWall(verticalWallPosition, Direction.WEST);
        Assertions.assertFalse(board.isWallAt(verticalWallPosition, Direction.WEST),
                "Vertical wall shouldn't be placed at " + verticalWallPosition);

    }

    @Test
    void checkIfPawnCanBePlacedOnBoard() {
        Board board = new Board();
        Position pawnPosition = new Position(3, 3);
        Pawn pawn = new Pawn(1, 1, pawnPosition);

        // Place the pawn on the board
        Assertions.assertTrue(board.placePawnAt(pawn, pawnPosition),
                "Pawn should be placed at " + pawnPosition);
        var pawnOnBoard = board.getCellAt(pawnPosition).getOptionalPawn();
        Assertions.assertTrue(pawnOnBoard.isPresent(),
                "Pawn should be placed at " + pawnPosition);
        Assertions.assertEquals(pawn, pawnOnBoard.get(),
                "Pawn in cell should match the one placed");
        Assertions.assertEquals(pawnPosition, pawnOnBoard.get().getPosition(),
                "Pawn position should be updated to " + pawnPosition);
    }

    @Test
    void checkIfPawnCannotBePlacedOutsideBoard() {
        Board board = new Board();
        Position pawnPosition1 = new Position(1, board.SIZE);
        Position pawnPosition2 = new Position(board.SIZE, 1);
        Pawn pawn1 = new Pawn(1, 1, pawnPosition1);
        Pawn pawn2 = new Pawn(2, 1, pawnPosition2);

        // Try to place the pawn outside the board
        Assertions.assertThrows(OutOfBoardException.class, () -> {
            board.placePawnAt(pawn1, pawnPosition1);
        }, "Placing a pawn outside the board should throw an exception");

        Assertions.assertThrows(OutOfBoardException.class, () -> {
            board.placePawnAt(pawn2, pawnPosition2);
        }, "Placing a pawn outside the board should throw an exception");
    }

    @Test
    void checkIfPawnCannotBePlacedOnOccupiedCell() {
        Board board = new Board();
        Position pawnPosition = new Position(3, 3);
        Pawn pawn1 = new Pawn(1, 1, pawnPosition);
        Pawn pawn2 = new Pawn(2, 1, pawnPosition);

        // Place the first pawn
        Assertions.assertTrue(board.placePawnAt(pawn1, pawnPosition),
                "First pawn should be placed at " + pawnPosition);

        // Try to place the second pawn on the same position
        Assertions.assertFalse(board.placePawnAt(pawn2, pawnPosition),
                "Second pawn shouldn't be placed at " + pawnPosition + " because it's occupied");
    }

    @Test
    void checkIfPawnCanBeMovedOnNorth() {
        Board board = new Board();
        Position initialPosition = new Position(3, 3);
        Pawn pawn = new Pawn(1, 1, initialPosition);

        Position newPositionNorth = new Position(3, 2);
        Pawn pawnPositionNorth = new Pawn(1, 1, newPositionNorth);

        // Place the pawn on the board
        board.placePawnAt(pawn, initialPosition);

        // Move the pawn to a new position
        Assertions.assertTrue(board.movePawnAt(initialPosition, Direction.NORTH),
                "Pawn should be moved to " + newPositionNorth);
        Assertions.assertTrue(board.getCellAt(initialPosition).getOptionalPawn().isEmpty(),
                "Pawn position should be updated to " + newPositionNorth);
        Assertions.assertEquals(board.getCellAt(newPositionNorth).getOptionalPawn().get(),
                pawnPositionNorth, "Pawn should be equals to " + pawnPositionNorth);
    }

    @Test
    void checkIfPawnCanBeMovedOnEast() {
        Board board = new Board();
        Position initialPosition = new Position(3, 3);
        Pawn pawn = new Pawn(1, 1, initialPosition);

        Position newPositionEast = new Position(4, 3);
        Pawn pawnPositionEast = new Pawn(1, 1, newPositionEast);

        // Place the pawn on the board
        board.placePawnAt(pawn, initialPosition);

        // Move the pawn to a new position
        Assertions.assertTrue(board.movePawnAt(initialPosition, Direction.EAST),
                "Pawn should be moved to " + newPositionEast);
        Assertions.assertTrue(board.getCellAt(initialPosition).getOptionalPawn().isEmpty(),
                "Pawn position should be updated to " + newPositionEast);
        Assertions.assertEquals(board.getCellAt(newPositionEast).getOptionalPawn().get(),
                pawnPositionEast, "Pawn should be equals to " + pawnPositionEast);
    }

    @Test
    void checkIfPawnCanBeMovedOnSouth() {
        Board board = new Board();
        Position initialPosition = new Position(3, 3);
        Pawn pawn = new Pawn(1, 1, initialPosition);

        Position newPositionSouth = new Position(3, 4);
        Pawn pawnPositionSouth = new Pawn(1, 1, newPositionSouth);

        // Place the pawn on the board
        board.placePawnAt(pawn, initialPosition);

        // Move the pawn to a new position
        Assertions.assertTrue(board.movePawnAt(initialPosition, Direction.SOUTH),
                "Pawn should be moved to " + newPositionSouth);
        Assertions.assertTrue(board.getCellAt(initialPosition).getOptionalPawn().isEmpty(),
                "Pawn position should be updated to " + newPositionSouth);
        Assertions.assertEquals(board.getCellAt(newPositionSouth).getOptionalPawn().get(),
                pawnPositionSouth, "Pawn should be equals to " + pawnPositionSouth);
    }

    @Test
    void checkIfPawnCanBeMovedOnWest() {
        Board board = new Board();
        Position initialPosition = new Position(3, 3);
        Pawn pawn = new Pawn(1, 1, initialPosition);

        Position newPositionWest = new Position(2, 3);
        Pawn pawnPositionWest = new Pawn(1, 1, newPositionWest);

        // Place the pawn on the board
        board.placePawnAt(pawn, initialPosition);

        // Move the pawn to a new position
        Assertions.assertTrue(board.movePawnAt(initialPosition, Direction.WEST),
                "Pawn should be moved to " + newPositionWest);
        Assertions.assertTrue(board.getCellAt(initialPosition).getOptionalPawn().isEmpty(),
                "Pawn position should be updated to " + newPositionWest);
        Assertions.assertEquals(board.getCellAt(newPositionWest).getOptionalPawn().get(),
                pawnPositionWest, "Pawn should be equals to " + pawnPositionWest);
    }

    @Test
    void checkIfPawnCannotBeMovedOnAnotherPawn() {
        Board board = new Board();
        Position initialPosition1 = new Position(3, 3);
        Position initialPosition2 = new Position(3, 4);
        Pawn pawn1 = new Pawn(1, 1, initialPosition1);
        Pawn pawn2 = new Pawn(2, 1, initialPosition2);

        // Place the pawns on the board
        board.placePawnAt(pawn1, initialPosition1);
        board.placePawnAt(pawn2, initialPosition2);

        // Try to move pawn1 to the position of pawn2
        Assertions.assertFalse(board.movePawnAt(initialPosition1, Direction.SOUTH),
                "Pawn should not be moved to " + initialPosition2 + " because it's occupied by another pawn");
    }

    @Test
    void checkIfPawnCannotBeMovedOutsideBoard() {
        Board board = new Board();
        Position initialPosition = new Position(0, 0);
        Pawn pawn = new Pawn(1, 1, initialPosition);

        // Place the pawn on the board
        board.placePawnAt(pawn, initialPosition);

        // Try to move the pawn outside the board
        Assertions.assertThrows(OutOfBoardException.class, () -> {
            board.movePawnAt(initialPosition, Direction.NORTH); // Moving up should go out of bounds
        }, "Moving a pawn outside the board should throw an exception");
    }

    @Test
    void checkIfPawnCannotBeMovedOnWallNorth() {
        Board board = new Board();
        Position initialPosition = new Position(3, 3);
        Pawn pawn = new Pawn(1, 1, initialPosition);

        // Place the pawn on the board
        board.placePawnAt(pawn, initialPosition);

        // Place a horizontal wall at (3, 3)
        board.placeWall(initialPosition, Direction.NORTH);

        // Try to move the pawn over the wall
        Assertions.assertFalse(board.movePawnAt(initialPosition, Direction.NORTH),
                "Pawn should not be moved over a wall at " + initialPosition);
    }

    @Test
    void checkIfPawnCannotBeMovedOnWallEast() {
        Board board = new Board();
        Position initialPosition = new Position(3, 3);
        Pawn pawn = new Pawn(1, 1, initialPosition);

        // Place the pawn on the board
        board.placePawnAt(pawn, initialPosition);

        // Place a vertical wall at (3, 3)
        board.placeWall(initialPosition, Direction.EAST);

        // Try to move the pawn over the wall
        Assertions.assertFalse(board.movePawnAt(initialPosition, Direction.EAST),
                "Pawn should not be moved over a wall at " + initialPosition);
    }

    @Test
    void checkIfPawnCannotBeMovedOnWallSouth() {
        Board board = new Board();
        Position initialPosition = new Position(3, 3);
        Pawn pawn = new Pawn(1, 1, initialPosition);

        // Place the pawn on the board
        board.placePawnAt(pawn, initialPosition);

        // Place a horizontal wall at (3, 3)
        board.placeWall(initialPosition, Direction.SOUTH);

        // Try to move the pawn over the wall
        Assertions.assertFalse(board.movePawnAt(initialPosition, Direction.SOUTH),
                "Pawn should not be moved over a wall at " + initialPosition);
    }

    @Test
    void checkIfPawnCannotBeMovedOnWallWest() {
        Board board = new Board();
        Position initialPosition = new Position(3, 3);
        Pawn pawn = new Pawn(1, 1, initialPosition);

        // Place the pawn on the board
        board.placePawnAt(pawn, initialPosition);

        // Place a vertical wall at (3, 3)
        Position wallPosition = new Position(3, 3);
        board.placeWall(wallPosition, Direction.WEST);

        // Try to move the pawn over the wall
        Assertions.assertFalse(board.movePawnAt(initialPosition, Direction.WEST),
                "Pawn should not be moved over a wall at " + wallPosition);
    }

    @Test
    void checkIfPawnCannotMoveWhenLocked(){
        Board board = new Board();
        Position initialPosition = new Position(3, 3);
        Pawn pawn = new Pawn(1, 1, initialPosition);
        board.placePawnAt(pawn, initialPosition);
        board.placeWall(initialPosition,Direction.NORTH);
        board.placeWall(initialPosition,Direction.EAST);
        board.placeWall(initialPosition,Direction.WEST);

        Assertions.assertTrue(board.isPawnCanMove(pawn));

        board.placeWall(initialPosition,Direction.SOUTH);

        Assertions.assertFalse(board.isPawnCanMove(pawn));

        board.removeWall(initialPosition,Direction.SOUTH);
        board.placePawnAt(new Pawn(2,2, initialPosition.move(Direction.SOUTH)), initialPosition.move(Direction.SOUTH));
        Assertions.assertFalse(board.isPawnCanMove(pawn));

    }

    @Test
    void checkClosedArea(){
        Board board = new Board();

        Pawn pawn1 = new Pawn(1, 1, new Position(0, 1));
        Pawn pawn2 = new Pawn(2, 2, new Position(1, 3));

        board.placePawnAt(pawn1, new Position(0,1));
        board.placePawnAt(pawn2, new Position(1,3));

        board.placeWall(new Position(0, 1), Direction.NORTH);
        board.placeWall(new Position(0, 1), Direction.EAST);

        board.placeWall(new Position(0, 2), Direction.EAST);

        board.placeWall(new Position(1, 2), Direction.NORTH);
        board.placeWall(new Position(1, 2), Direction.EAST);

        board.placeWall(new Position(2, 3), Direction.NORTH);
        board.placeWall(new Position(2, 3), Direction.EAST);
        board.placeWall(new Position(2, 3), Direction.SOUTH);

        board.placeWall(new Position(1, 3), Direction.SOUTH);
        board.placeWall(new Position(0, 3), Direction.SOUTH);

        var cells = new HashSet<Cell>();

        var cell1 = new Cell(new Position(0,1));
        cell1.setOptionalPawn(Optional.of(pawn1));
        var cell2 = new Cell(new Position(0,2));
        var cell3 = new Cell(new Position(1,2));
        var cell4 = new Cell(new Position(1,3));
        cell4.setOptionalPawn(Optional.of(pawn2));
        var cell5 = new Cell(new Position(2,3));
        var cell6 = new Cell(new Position(0,3));

        cells.add(cell1);
        cells.add(cell2);
        cells.add(cell3);
        cells.add(cell4);
        cells.add(cell5);
        cells.add(cell6);
        System.out.println(board.displayBoard());

        var actualSet = board.getAreaFromPosition(new Position(1,3));
        Assertions.assertTrue(actualSet.equals(cells));
    }

}
