package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class CellTest {

    @Test
    public void CellIsNotOccupiedWhenInitialized() {
        Cell cell = new Cell();
        Assertions.assertFalse(cell.isOccuped(), "Cell should not be occupied when initialized");
        Assertions.assertTrue(cell.getOptionalPawn().isEmpty(), "Cell should not contain a pawn when initialized");
    }
    @Test
    void CellIsCoccupedWhenPawnIsSet() {
        Cell cell = new Cell();
        Pawn pawn = new Pawn(1, 1, new Position(0, 0));
        cell.setOptionalPawn(Optional.of(pawn));

        Assertions.assertTrue(cell.isOccuped(), "Cell should be occupied after setting a pawn");
        Assertions.assertTrue(cell.getOptionalPawn().isPresent(), "Cell should contain a pawn");
        Assertions.assertEquals(pawn, cell.getOptionalPawn().get(), "Pawn in cell should match the one set");
    }

    @Test
    void CellIsNotOccupedWhenPawnIsRemoved() {
        Cell cell = new Cell();
        Pawn pawn = new Pawn(1, 1, new Position(0, 0));
        cell.setOptionalPawn(Optional.of(pawn));

        // Remove the pawn
        cell.setOptionalPawn(Optional.empty());

        // Remplace les assert par ceux de junit
        Assertions.assertFalse(cell.isOccuped(), "Cell should not be occupied after removing the pawn");
        Assertions.assertTrue(cell.getOptionalPawn().isEmpty(), "Cell should not contain a pawn after removal");
    }
}
