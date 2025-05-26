package model;

import exception.OutOfBoardException;

import java.util.NoSuchElementException;
import java.util.Optional;

public class Board {
    public final int SIZE;
    private Cell[][] grille;
    private boolean[][] horizontalWalls;
    private boolean[][] verticalWalls;
    private boolean isStarted;
    private int pawnsOnBoard;

    public Board(int size){
        this.SIZE = size;
        this.grille = new Cell[size][size];
        initializeCell();

        this.horizontalWalls = new boolean[SIZE][SIZE + 1];
        this.verticalWalls = new boolean[SIZE + 1][SIZE];
        this.initializeBorderWalls();

        this.isStarted = false;
    }

    private void initializeCell(){
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                grille[x][y] = new Cell();
            }
        }
    }
    private void initializeBorderWalls() {
        // Murs du haut et du bas
        for (int x = 0; x < SIZE; x++) {
            horizontalWalls[x][0] = true;        // Bordure haut
            horizontalWalls[x][SIZE] = true;     // Bordure bas
        }

        // Murs de gauche et de droite
        for (int y = 0; y < SIZE; y++) {
            verticalWalls[0][y] = true;          // Bordure gauche
            verticalWalls[SIZE][y] = true;       // Bordure droite
        }
    }

    public boolean isPositionOnBoard(Position position){
        return position.getX() >= 0 && position.getX() < SIZE && position.getY() >= 0 && position.getY() < SIZE;
    }

    public boolean placeHorizontalWall(Position position) {
        if (position.getX() >= 0 && position.getX() < SIZE
                && position.getY() >= 0 && position.getY() < SIZE + 1) {
            horizontalWalls[position.getX()][position.getY()] = true;
            return true;
        }
        return false;
    }

    public boolean placeVerticalWall(Position position) {
        if (position.getX() >= 0 && position.getX() < SIZE + 1
                && position.getY() >= 0 && position.getY() < SIZE) {
            verticalWalls[position.getX()][position.getY()] = true;
            return true;
        }
        return false;
    }

    public boolean isHorizontalWallAt(Position position) {
        return horizontalWalls[position.getX()][position.getY()];
    }

    public boolean isVerticalWallAt(Position position) {
        return verticalWalls[position.getX()][position.getY()];
    }

    private Cell cellAt(Position position){
        if (!isPositionOnBoard(position)){
            throw new OutOfBoardException(position);
        }
        return grille[position.getX()][position.getY()];
    };

    /**
     * Ajoute un pion à une poisition
     * @param pawn
     * @param position
     * @return true si le pion a pu être placé, false sinon
     */
    public boolean placePawnAt(Pawn pawn, Position position){
        var celltoModify = cellAt(position);
        if (!celltoModify.isOccuped()){
            pawn.setPosition(position);
            celltoModify.setOptionalPawn(Optional.of(pawn));
            return true;
        }
        return false;
    }

    /**
     * Retire un pion à une poisition
     * @param position
     * @return Optional contenant un pion si un pion a pu être retiré, vide sinon
     */
    public Optional<Pawn> retirePawnAt(Position position){
        var celltoModify = cellAt(position);
        var pawn = celltoModify.getOptionalPawn();
        celltoModify.setOptionalPawn(Optional.ofNullable(null));
        return pawn;
    }

    /**
     * Bouge un pion à poisition dans une direction
     * @param position
     * @param direction
     * @return true si le pion a pu être déplacé, flase sinon
     */
    public boolean moovePawnAt(Position position, Direction direction){
        var cellToCheck = cellAt(position);
        if (cellToCheck.isOccuped()){
            var nextPosition = new Position(position.getX() + direction.getDx(),
                    position.getY() + direction.getDy());
            try {
                if (!cellAt(nextPosition).isOccuped()){
                    try {
                        var pawn = retirePawnAt(position);
                        placePawnAt(pawn.get(), nextPosition);
                    } catch (NoSuchElementException e){
                        return false;
                    }
                    return true;
                }
            } catch (OutOfBoardException e){
                return false;
            }
        }
        return false;
    }

    /**
     * Affiche le plateau avec les cases et les murs
     */
    public void displayBoard() {
        var boardDisplayed = new StringBuilder();
        boardDisplayed.append("=== Plateau avec coordonnées ===").append("\n");

        // Numéros de colonnes
        boardDisplayed.append("    ");
        for (int x = 0; x < SIZE; x++) {
            boardDisplayed.append("  ").append(x + 1).append(" ");
        }
        boardDisplayed.append("\n");

        for (int y = 0; y < SIZE + 1; y++) {
            // Ligne des murs horizontaux
            boardDisplayed.append("    ");
            for (int x = 0; x < SIZE; x++) {
                boardDisplayed.append("+");
                if (x < horizontalWalls.length && x < horizontalWalls[x].length && horizontalWalls[x][y]) {
                    boardDisplayed.append("━━━");
                } else {
                    boardDisplayed.append("   ");
                }
            }
            boardDisplayed.append("+").append("\n");

            // Ligne des cases
            if (y < SIZE) {
                boardDisplayed.append(" ").append(y + 1).append("  "); // Numéro de ligne

                for (int x = 0; x < SIZE; x++) {
                    if (x < verticalWalls[x].length && verticalWalls[x][y]) {
                        boardDisplayed.append("┃");
                    } else {
                        boardDisplayed.append(" ");
                    }

                    if (x < SIZE) {
                        Cell cell = grille[x][y];
                        boardDisplayed.append(" ").append(cell.display()).append(" ");
                    }
                }
                boardDisplayed.append("┃").append("\n");
            }
        }
        System.out.println(boardDisplayed.toString());

        // Informations supplémentaires
        System.out.println("Pions sur le plateau: " + pawnsOnBoard);
        System.out.println("Partie commencée: " + (isStarted ? "Oui" : "Non"));
    }

    /**
     * Affiche 3 plateaux (pions, murs horizontaux, murs verticaux)
     */
    public void displayBoardCompact() {
        System.out.println("=== Plateau Compact ===");

        // Affichage des numéros de colonnes
        System.out.print("  ");
        for (int x = 0; x < SIZE; x++) {
            System.out.print(" " + x);
        }
        System.out.println();

        // Affichage ligne par ligne
        for (int y = 0; y < SIZE; y++) {
            System.out.print(y + " ");
            for (int x = 0; x < SIZE; x++) {
                Cell cell = grille[x][y];
                if (cell == null) {
                    System.out.print(" .");
                } else {
                    System.out.print(" " + cell.display().charAt(0)); // Premier caractère seulement
                }
            }
            System.out.println();
        }

        // Affichage des murs (optionnel)
        System.out.println("Murs horizontaux:");
        System.out.print("  ");
        for (int x = 0; x < horizontalWalls.length; x++) {
            System.out.print(" " + x);
        }
        System.out.println();
        for (int y = 0; y < SIZE + 1; y++) {
            System.out.print(y + " ");
            for (int x = 0; x < SIZE; x++) {
                System.out.print(horizontalWalls[x][y] ? " #" : " .");
            }
            System.out.println();
        }

        System.out.println("Murs verticaux:");
        System.out.print("  ");
        for (int x = 0; x < horizontalWalls.length; x++) {
            System.out.print(" " + x);
        }
        System.out.println();
        for (int y = 0; y < SIZE; y++) {
            System.out.print(y + " ");
            for (int x = 0; x < SIZE + 1; x++) {
                System.out.print(verticalWalls[x][y] ? " #" : " .");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        var board = new Board(7);
        board.placeHorizontalWall(new Position(1,2));
        board.placeVerticalWall(new Position(3,2));


        var pawn = new Pawn(1,1,new Position(0,0));
        board.placePawnAt(pawn, new Position(6, 6));
        board.displayBoard();
        /*
        pawn = board.retirePawnAt(new Position(4,5)).get();
        board.displayBoard();
        board.placePawnAt(pawn, new Position(4,6));
        board.displayBoard();
        */
        board.moovePawnAt(new Position(6,6), Direction.DROITE);
        board.displayBoard();
        //board.displayBoardCompact();
    }
}
