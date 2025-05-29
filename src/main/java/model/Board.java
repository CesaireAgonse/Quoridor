package model;

import exception.OutOfBoardException;

import java.util.NoSuchElementException;
import java.util.Optional;

public class Board {
    public final int SIZE;
    private final Cell[][] grille;
    private boolean[][] horizontalWalls;
    private boolean[][] verticalWalls;
    private int pawnsOnBoard;

    public Board() {
        this(7);
    }
    public Board(int size){
        if (size < 7) {
            throw new IllegalArgumentException("La taille du plateau doit être supérieure ou égale à 7.");
        }
        this.SIZE = size;
        this.grille = new Cell[size][size];
        initializeCell();

        this.horizontalWalls = new boolean[SIZE][SIZE + 1];
        this.verticalWalls = new boolean[SIZE + 1][SIZE];
        this.initializeBorderWalls();
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
            if (horizontalWalls[position.getX()][position.getY()]) {
                return false; // Un mur existe déjà à cette position
            }
            horizontalWalls[position.getX()][position.getY()] = true;
            return true;
        }
        return false;
    }

    public boolean removeHorizontalWall(Position position) {
        if (position.getX() >= 0 && position.getX() < SIZE
                && position.getY() >= 0 && position.getY() < SIZE + 1) {
            if (!horizontalWalls[position.getX()][position.getY()]) {
                return false; // Aucun mur à cette position
            }
            horizontalWalls[position.getX()][position.getY()] = false;
            return true;
        }
        return false;
    }

    public boolean placeVerticalWall(Position position) {
        if (position.getX() >= 0 && position.getX() < SIZE + 1
                && position.getY() >= 0 && position.getY() < SIZE) {
            if (verticalWalls[position.getX()][position.getY()]) {
                return false; // Un mur existe déjà à cette position
            }
            verticalWalls[position.getX()][position.getY()] = true;
            return true;
        }
        return false;
    }

    public boolean removeVerticalWall(Position position) {
        if (position.getX() >= 0 && position.getX() < SIZE + 1
                && position.getY() >= 0 && position.getY() < SIZE) {
            if (!verticalWalls[position.getX()][position.getY()]) {
                return false; // Aucun mur à cette position
            }
            verticalWalls[position.getX()][position.getY()] = false;
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

    public Cell getCellAt(Position position){
        if (!isPositionOnBoard(position)){
            throw new OutOfBoardException(position);
        }
        return grille[position.getX()][position.getY()];
    };

    /**
     * Ajoute un pion à une poisition
     * @param pawn pion à ajouter
     * @param position position à laquelle le pion doit être placé
     * @return true si le pion a pu être placé, false sinon
     */
    public boolean placePawnAt(Pawn pawn, Position position){
        if (!isPositionOnBoard(position)) {
            throw new OutOfBoardException(position);
        }
        var celltoModify = getCellAt(position);
        if (!celltoModify.isOccuped()){
            pawn.setPosition(position);
            celltoModify.setOptionalPawn(Optional.of(pawn));
            pawnsOnBoard++;
            return true;
        }
        return false;
    }

    /**
     * Retire un pion à une poisition
     * @param position position du pion à retirer
     * @return Optional contenant un pion si un pion a pu être retiré, vide sinon
     */
    public Optional<Pawn> removePawnAt(Position position){
        var celltoModify = getCellAt(position);
        var optionalPawn = celltoModify.getOptionalPawn();
        if (optionalPawn.isPresent()) {
            pawnsOnBoard--;
        }
        celltoModify.setOptionalPawn(Optional.ofNullable(null));
        return optionalPawn;
    }

    /**
     * Bouge un pion à poisition dans une direction
     * @param position position du pion à déplacer
     * @param direction direction dans laquelle le pion doit être déplacé
     * @return true si le pion a pu être déplacé, flase sinon
     */
    public boolean movePawnAt(Position position, Direction direction){
        var cellToCheck = getCellAt(position);
        if (!cellToCheck.isOccuped()){
            throw new IllegalArgumentException("Aucun pion à la position " + position);
        }
        try {
            var nextPosition = new Position(position.getX() + direction.getDx(),
                    position.getY() + direction.getDy());
            if (getCellAt(nextPosition).isOccuped() || isWallBetween(position, nextPosition)){
                return false;
            }
            var pawn = removePawnAt(position);
            if (pawn.isEmpty()) {
                throw new NoSuchElementException("Aucun pion trouvé à la position " + position);
            }
            placePawnAt(pawn.get(), nextPosition);
        } catch (IllegalArgumentException e) {
            throw new OutOfBoardException("Le pion à la position [" + position + "] ne peut pas être déplacé en dehors du plateau: " + direction);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Aucun pion trouvé à la position: " + position, e);
        }

        return true;
    }

    /**
     * Vérifie si il existe un mur entre deux positions adjacentes
     * @param position1 position 1
     * @param position2 position 2
     * @return true si un mur existe entre les deux positions, false sinon
     */
    public boolean isWallBetween(Position position1, Position position2) {
        if (!isPositionOnBoard(position1) || !isPositionOnBoard(position2)) {
            throw new OutOfBoardException(position1);
        }

        // Vérification des positions pour s'assurer qu'elles soient bien adjacentes
        if (position1.distanceTo(position2) != 1) {
            throw new IllegalArgumentException("Les positions doivent être adjacentes.");
        }

        // Vérification des murs horizontaux
        if (position1.getY() != position2.getY()) {
            int maxY = Math.max(position1.getY(), position2.getY());
            var newPos = new Position(position1.getX(), maxY);
            return isHorizontalWallAt(newPos);
        }

        // Vérification des murs verticaux
        if (position1.getX() != position2.getX()) {
            int maxX = Math.max(position1.getX(), position2.getX());
            var newPos = new Position(maxX, position1.getY());
            return isVerticalWallAt(newPos);
        }

        return false; // Jamais atteint, mais pour la complétion
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
        for (int x = 0; x < verticalWalls.length; x++) {
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


        var pawn1 = new Pawn(1,1,new Position(0,0));
        board.placePawnAt(pawn1, new Position(2, 2));

        var wall = board.isWallBetween(new Position(3, 2), new Position(2, 2));
        System.out.println(wall);

        board.displayBoard();
        board.movePawnAt(new Position(2,2), Direction.BAS);
        board.displayBoard();
        board.displayBoardCompact();
    }
}
