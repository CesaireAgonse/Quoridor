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
    public boolean equals(Object obj) {
        if (obj instanceof Cell){
            Cell other = (Cell) obj;
            return this.isOccuped == other.isOccuped
                    && this.optionalPawn.equals(other.optionalPawn)
                    && this.position.equals(other.position);
        }
        return false;
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
