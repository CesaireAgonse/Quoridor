package model;

import exception.OutOfBoardException;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Board {
    public final int SIZE;
    private final Cell[][] grille;
    private final boolean[][] horizontalWalls;
    private final boolean[][] verticalWalls;
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

    public int getPawnsOnBoard() {
        return pawnsOnBoard;
    }

    private void initializeCell(){
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                grille[x][y] = new Cell(new Position(x, y));
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

    /**
     * Vérifie si une position est sur le plateau
     * @param position position à vérifier
     * @return true si la position est sur le plateau, false sinon
     */
    public boolean isPositionOnBoard(Position position){
        return position.getX() >= 0 && position.getX() < SIZE && position.getY() >= 0 && position.getY() < SIZE;
    }

    private boolean placeHorizontalWall(Position position) {
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
    private boolean removeHorizontalWall(Position position) {
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

    private boolean placeVerticalWall(Position position) {
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
    private boolean removeVerticalWall(Position position) {
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

    private boolean isHorizontalWallAt(Position position) {
        return horizontalWalls[position.getX()][position.getY()];
    }
    private boolean isVerticalWallAt(Position position) {
        return verticalWalls[position.getX()][position.getY()];
    }

    public boolean placeWall(Position position, Direction direction) {
        switch (direction){
            case NORTH -> {
                return placeHorizontalWall(new Position(position.getX(), position.getY()));
            }
            case SOUTH -> {
                return placeHorizontalWall(new Position(position.getX(), position.getY() + 1));
            }
            case EAST -> {
                return placeVerticalWall(new Position(position.getX() + 1, position.getY()));
            }
            case WEST -> {
                return placeVerticalWall(new Position(position.getX(), position.getY()));
            }
            default -> {
                throw new IllegalArgumentException("Direction invalide: " + direction);
            }
        }
    }
    public boolean removeWall(Position position, Direction direction) {
        switch (direction){
            case NORTH -> {
                return removeHorizontalWall(new Position(position.getX(), position.getY()));
            }
            case SOUTH -> {
                return removeHorizontalWall(new Position(position.getX(), position.getY() + 1));
            }
            case EAST -> {
                return removeVerticalWall(new Position(position.getX() + 1, position.getY()));
            }
            case WEST -> {
                return removeVerticalWall(new Position(position.getX(), position.getY()));
            }
            default -> {
                throw new IllegalArgumentException("Direction invalide: " + direction);
            }
        }
    }

    public boolean isWallAt(Position position, Direction direction) {
        switch (direction) {
            case NORTH:
                return isHorizontalWallAt(new Position(position.getX(), position.getY()));
            case SOUTH:
                return isHorizontalWallAt(new Position(position.getX(), position.getY() + 1));
            case EAST:
                return isVerticalWallAt(new Position(position.getX() + 1, position.getY()));
            case WEST:
                return isVerticalWallAt(new Position(position.getX(), position.getY()));
            default:
                throw new IllegalArgumentException("Direction invalide: " + direction);
        }
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
            var nextPosition = position.move(direction);
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
     * Vérifie si un pion peut se déplacer
     * @param pawn
     * @return
     */
    public boolean isPawnCanMove(Pawn pawn){
        var position = pawn.getPosition();
        var wallNumber = 0;
        var pawnNumber = 0;
        for (var direction : Direction.values()){
            if (isWallAt(position, direction)){
                wallNumber++;
            };

            Position nextPosition;
            try {
                nextPosition = position.move(direction);
            } catch (OutOfBoardException e) {
                continue; // Ignore les exceptions pour les positions hors plateau
            } catch (IllegalArgumentException e) {
                continue; // Ignore les positions négatives
            }

            try {
                if (getCellAt(nextPosition).isOccuped()) {
                    pawnNumber++;
                }
            } catch (OutOfBoardException e) {
                continue; // Ignore les exceptions pour les positions hors plateau
            } catch (IllegalArgumentException e) {
                continue; // Ignore les positions négatives
            }
        }
        return wallNumber < 4 || pawnNumber < 4;
    }

    public boolean isPawnCanPlaceWall(Pawn pawn){
        var position = pawn.getPosition();
        var wallNumber = 0;
        for (var direction : Direction.values()){
            if (isWallAt(position, direction)){
                wallNumber++;
            };
        }
        return wallNumber < 4;
    }

    /**
     * Vérifie s'il existe un mur entre deux positions adjacentes
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
     * Récupère toute une zone fermé à partir d'une position
     * @param position La position servant de point de départ
     * @return une zone fermé représenté par des cases de la grille
     */
    public HashSet<Cell> getAreaFromPosition(Position position, HashSet<Cell> cells){
        cells.add(getCellAt(position));
        System.out.println(cells);
        for (var direction : Direction.values()){
            Position nextPosition;
            if (!isWallAt(position, direction)){
                nextPosition = position.move(direction);
                if (!cells.contains(getCellAt(nextPosition))){
                    cells.addAll(getAreaFromPosition(nextPosition, cells));
                }
            }
        }

        return cells;
    }

    /**
     * Affiche le plateau avec les cases et les murs
     */
    public String displayBoard() {
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

        // Informations supplémentaires
        return boardDisplayed.append("Pions sur le plateau: " + pawnsOnBoard).toString();
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
    }

    public boolean isPawnCanMoveTo(Pawn selectedPawn, Position nextPosition, int alreadyMoved) {
        if (!isPositionOnBoard(nextPosition)) {
            return false; // La position n'est pas sur le plateau
        }

        if (alreadyMoved >= 2) {
            return false; // Le pion ne peut pas se déplacer plus de 2 fois
        }

        var currentPosition = selectedPawn.getPosition();
        if (currentPosition.distanceTo(nextPosition) > 2){
            return false; // Le pion ne peut pas se déplacer de plus de 2 cases
        }
        //Comment aller de currentPosition à nextPosition
        if (currentPosition.equals(nextPosition) && alreadyMoved <= 2) {
            return true; // Le pion peut rester sur place
        }
        // Vérifier si le pion peut se déplacer d'une case
        if (currentPosition.distanceTo(nextPosition) == 1 && alreadyMoved <= 1) {
            if (getCellAt(nextPosition).isOccuped() || isWallBetween(currentPosition, nextPosition)) {
                return false; // Il y a un mur ou un autre pion à la position suivante
            }
            return true; // Le pion peut se déplacer d'une case
        }

        // Vérifier si le pion peut se déplacer de deux cases
        if (currentPosition.distanceTo(nextPosition) == 2 && alreadyMoved <= 0) {
            int deltaX = nextPosition.getX() - currentPosition.getX();
            int deltaY = nextPosition.getY() - currentPosition.getY();

            // Cas 1: Déplacement en ligne droite (2 cases dans une direction)
            if (deltaX == 0 || deltaY == 0) {
                // Mouvement vertical ou horizontal de 2 cases
                Position intermediatePosition;
                if (deltaX == 0) {
                    // Mouvement vertical
                    intermediatePosition = new Position(currentPosition.getX(),
                            currentPosition.getY() + deltaY/2);
                } else {
                    // Mouvement horizontal
                    intermediatePosition = new Position(currentPosition.getX() + deltaX/2,
                            currentPosition.getY());
                }

                // Vérifier qu'on peut aller à la position intermédiaire et à la destination
                return !isWallBetween(currentPosition, intermediatePosition) &&
                        !getCellAt(intermediatePosition).isOccuped() &&
                        !isWallBetween(intermediatePosition, nextPosition) &&
                        !getCellAt(nextPosition).isOccuped();
            }

            // Cas 2 et 3: Déplacement en L (1 case + 1 case perpendiculaire)
            if (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 1) {
                // Chemin 1: horizontal puis vertical
                Position path1Intermediate = new Position(currentPosition.getX() + deltaX,
                        currentPosition.getY());
                boolean path1Valid = !isWallBetween(currentPosition, path1Intermediate) &&
                        !getCellAt(path1Intermediate).isOccuped() &&
                        !isWallBetween(path1Intermediate, nextPosition) &&
                        !getCellAt(nextPosition).isOccuped();

                // Chemin 2: vertical puis horizontal
                Position path2Intermediate = new Position(currentPosition.getX(),
                        currentPosition.getY() + deltaY);
                boolean path2Valid = !isWallBetween(currentPosition, path2Intermediate) &&
                        !getCellAt(path2Intermediate).isOccuped() &&
                        !isWallBetween(path2Intermediate, nextPosition) &&
                        !getCellAt(nextPosition).isOccuped();

                // Au moins un des deux chemins doit être valide
                return path1Valid || path2Valid;
            }

            return false;
        }

        return false; // Le pion ne peut pas se déplacer de cette manière

    }
}
