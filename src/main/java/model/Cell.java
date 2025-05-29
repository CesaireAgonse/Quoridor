package model;

import java.util.Optional;

public class Cell {
    private boolean isOccuped;
    private Optional<Pawn> optionalPawn;

    public Cell(){
        this.isOccuped = false;
        this.optionalPawn = Optional.empty();
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

    public String display() {
        if (optionalPawn.isPresent()){
            return optionalPawn.get().display();
        }
        return " ";
    }

    @Override
    public String toString() {
        return "Cell{" +
                "isOccuped=" + isOccuped +
                ", pawn=" + optionalPawn +
                '}';
    }
}
