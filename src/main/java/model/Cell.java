package model;

import java.util.Objects;
import java.util.Optional;

public class Cell {
    private boolean isOccuped;
    private Optional<Pawn> optionalPawn;
    private final Position position;

    public Cell(Position position){
        Objects.requireNonNull(position);
        this.isOccuped = false;
        this.optionalPawn = Optional.empty();
        this.position = position;
    }

    public boolean isOccuped() {
        return isOccuped;
    }

    private void setOccuped(boolean occuped) {
        isOccuped = occuped;
    }

    public Optional<Pawn> getOptionalPawn() {
        return optionalPawn;
    }

    public void setOptionalPawn(Optional<Pawn> optionalPawn) {

        if (optionalPawn.isEmpty()){
            setOccuped(false);
            this.optionalPawn = optionalPawn;
            return;
        }
        this.optionalPawn = optionalPawn;
        setOccuped(true);
    }

    public Position getPosition(){
        return position;
    }

    public String display() {
        if (optionalPawn.isPresent()){
            return optionalPawn.get().display();
        }
        return " ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return isOccuped == cell.isOccuped && Objects.equals(optionalPawn, cell.optionalPawn) && Objects.equals(position, cell.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isOccuped, optionalPawn, position);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "isOccuped=" + isOccuped +
                ", pawn=" + optionalPawn +
                ", position=" + position +
                '}';
    }
}
