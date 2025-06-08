package api.graphic;

import exception.OutOfBoardException;
import model.*;
import util.GameFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;

public class GameWindow extends JFrame {

    private static final Color[] PLAYER_COLORS = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
    private JPanel gamePanel;
    private JEditorPane infoTextArea;
    private final Game game;
    private ImageIcon backgroundTexture;
    private ImageIcon backgroundImage;
    private Pawn selectedPawn = null;
    private SubPlayerAction currentAction = SubPlayerAction.PLACE_PAWN;
    private int currentMovedCells = 0;
    private JButton specialButton;

    public GameWindow(Game game) {
        this.game = game;
        loadTexture();
        initializeWindow();
        createGameArea();
        setupBoardDisplay();
        createInfoArea();
        updateInfoDisplay();
        setVisible(true);


        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                SwingUtilities.invokeLater(() -> {
                    if (gamePanel != null) {
                        gamePanel.revalidate();
                        gamePanel.repaint();
                    }
                });
            }
        });
    }

    public enum SubPlayerAction {
        PLACE_PAWN,
        SELECT_PAWN,
        MOVE_PAWN,
        PLACE_WALL,
        DELETE_WALL;
    }

    private class BoardPanel extends JPanel {

        private int MARGIN = 25;
        private int boardSize;
        private int cellSize;
        private int startX;
        private int startY;

        public BoardPanel() {
            setOpaque(true);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    handleMouseClick(e.getX(), e.getY());
                }
            });
        }

        private void setBoardSize(int size) {
            this.boardSize = size;
        }
        private void setCellSize(int size) {
            this.cellSize = size;
        }
        private void setStartX(int startX) {
            this.startX = startX;
        }
        private void setStartY(int startY) {
            this.startY = startY;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (backgroundImage != null) {
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            } else {
                setBackground(Color.WHITE);
            }

            setBoardSize(game.getBoard().SIZE);

            // Calculer la taille disponible après les marges
            int availableWidth = getWidth() - 2 * MARGIN;
            int availableHeight = getHeight() - 2 * MARGIN;

            setCellSize(Math.min(availableWidth / boardSize, availableHeight / boardSize));

            // Centrer le plateau dans l'espace disponible (avec marges)
            int boardPixelSize = boardSize * cellSize;
            setStartX(MARGIN + (availableWidth - boardPixelSize) / 2);
            setStartY(MARGIN + (availableHeight - boardPixelSize) / 2);

            // Dessiner la texture de fond
            if (backgroundTexture != null) {
                g.drawImage(backgroundTexture.getImage(), startX, startY, boardPixelSize, boardPixelSize, this);
            }

            // Dessiner la grille
            drawGrid(g, boardPixelSize);

            // Dessiner les murs
            drawWalls(g);

            //dessiner les pions
            drawPawns(g);
        }

        private void drawGrid(Graphics g, int boardPixelSize) {
            g.setColor(Color.GRAY);
            for (int i = 0; i <= boardSize; i++) {
                g.drawLine(startX + i * cellSize, startY, startX + i * cellSize, startY + boardPixelSize);
                g.drawLine(startX, startY + i * cellSize, startX + boardPixelSize, startY + i * cellSize);
            }
        }

        private void drawWalls(Graphics g) {
            g.setColor(Color.BLACK);
            // Dessiner les murs horizontaux
            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    if (game.getBoard().isWallAt(new Position(x, y), Direction.NORTH)) {
                        drawWall(g, new Position(x, y), Direction.NORTH);
                    }

                    if (y == boardSize - 1 && game.getBoard().isWallAt(new Position(x, y), Direction.SOUTH)) {
                        drawWall(g, new Position(x, y), Direction.SOUTH);
                    }
                }
            }
            // Dessiner les murs verticaux
            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    if (game.getBoard().isWallAt(new Position(x, y), Direction.WEST)) {
                        drawWall(g, new Position(x, y), Direction.WEST);
                    }

                    if (x == boardSize - 1 && game.getBoard().isWallAt(new Position(x, y), Direction.EAST)) {
                        drawWall(g, new Position(x, y), Direction.EAST);
                    }
                }
            }
        }

        private void drawPawns(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pawnSize = cellSize / 2; // Taille du pion

            for (int playerIndex = 0; playerIndex < game.getPlayers().size(); playerIndex++) {
                Player player = game.getPlayers().get(playerIndex);
                Color playerColor = PLAYER_COLORS[playerIndex];

                for (Pawn pawn : player.getPawns()) {
                    Position pos;
                    try {
                        pos = pawn.getPosition();
                    } catch (IllegalStateException e) {
                        // Pion non placé sur le plateau
                        continue;
                    }

                    int pawnX = startX + pos.getX() * cellSize + cellSize / 2 - pawnSize / 2;
                    int pawnY = startY + pos.getY() * cellSize + cellSize / 2 - pawnSize / 2;

                    // Dessiner la surbrillance si ce pion est sélectionné

                    if (pawn == selectedPawn) {
                        g2d.setColor(Color.YELLOW);
                        g2d.fillOval(pawnX - 3, pawnY - 3, pawnSize + 6, pawnSize + 6);
                    }

                    // Dessiner le pion comme un cercle

                    g2d.setColor(playerColor);
                    g2d.fillOval(pawnX, pawnY, pawnSize, pawnSize);

                    // Contour noir
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(pawnX, pawnY, pawnSize, pawnSize);

                }
            }
        }

        private void drawWall(Graphics g, Position position, Direction direction){
            switch (direction) {
                case NORTH:
                    g.fillRect(startX + position.getX() * cellSize, startY + position.getY() * cellSize - 3, cellSize + 1, 6);
                    break;
                case SOUTH:
                    g.fillRect(startX + position.getX() * cellSize, startY + (position.getY() + 1) * cellSize - 3, cellSize + 1, 6);
                    break;
                case WEST:
                    g.fillRect(startX + position.getX() * cellSize - 3, startY + position.getY() * cellSize, 6, cellSize + 1);
                    break;
                case EAST:
                    g.fillRect(startX + (position.getX() + 1) * cellSize - 3, startY + position.getY() * cellSize, 6, cellSize + 1);
                    break;
            }
        }

        private void handleMouseClick(int mouseX, int mouseY) {
            // Convertir les coordonnées de souris en position de grille
            int gridX = (mouseX - startX) / cellSize;
            int gridY = (mouseY - startY) / cellSize;

            var clickedPosition = new Position(gridX, gridY);

            if (!isPositionValid(clickedPosition)) {
                throw new OutOfBoardException("Position cliquée en dehors de la grille: " + clickedPosition);
            }

            if (!game.isStarted()) {
                handleSetupPhase(clickedPosition);
            } else {
                handlePlayPhase(clickedPosition);
            }
        }

        private boolean isPositionValid(Position position) {
            return position.getX() >= 0 && position.getX() < boardSize &&
                   position.getY() >= 0 && position.getY() < boardSize;
        }


    }

    private void loadTexture() {
        try {
            backgroundTexture = new ImageIcon(Objects.requireNonNull(getClass().getResource("/bois.jpg")));
        } catch (Exception e) {
            System.out.println("Impossible de charger l'image de texture: " + e.getMessage());
            backgroundTexture = null;
        }

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/death_room.PNG"));
        } catch (Exception e) {
            System.out.println("Impossible de charger l'image d'arrière-plan: " + e.getMessage());
            backgroundImage = null;
        }
    }

    private void initializeWindow() {
        setTitle("Quoridor - Partie en cours");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Redessiner le plateau quand la fenêtre est redimensionnée
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                if (gamePanel != null) {
                    setupBoardDisplay();
                }
            }
        });
    }

    private void createGameArea() {
        gamePanel = new JPanel();

        // La zone de jeu occupe la majeure partie de la fenêtre
        add(gamePanel, BorderLayout.CENTER);
    }

    private void createInfoArea() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(250, 0));

        infoTextArea = new JEditorPane();
        infoTextArea.setContentType("text/html");
        infoTextArea.setEditable(false);
        infoTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        specialButton = new JButton("Utiliser capacité spéciale");
        specialButton.addActionListener(e -> useSpecialCapacity());
        specialButton.setVisible(false);
        infoPanel.add(specialButton, BorderLayout.SOUTH);
        infoPanel.add(infoTextArea, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
    }

    /**
     * Convertit l'index du joueur en une couleur hexadécimale.
     * @param playerIndex
     * @return La couleur hexadécimale au format #RRGGBB.
     */
    private String getColorHex(int playerIndex) {
        Color color = PLAYER_COLORS[playerIndex];
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Actualise la fenêtre en mettant à jour l'affichage des informations et en redessinant le plateau de jeu.
     */
    private void refreshWindow() {
        updateInfoDisplay();
        repaint();
    }

    /**
     * Met à jour l'affichage des informations en fonction de l'état du jeu.
     */
    private void updateInfoDisplay() {
        if (game.isStarted()) {
            updateInfoDisplayGameStarted();
        } else {
            updateInfoDisplaySetup();
        }
    }

    private void updateInfoDisplaySetup(){
        StringBuilder info = new StringBuilder();
        var currentPlayer = game.getCurrentPlayerIndex();
        info.append("<html><body>");
        info.append("<h2>PHASE DE PLACEMENT</h2>");
        info.append("<p><font color='").append(getColorHex(currentPlayer)).append("'>");
        info.append(game.getPlayers().get(currentPlayer).getName());
        info.append("</font>");
        info.append(" doit placer un pion sur le plateau.</p>");
        info.append("<h3>JOUEURS</h3>");
        for (int i = 0; i < game.getPlayers().size(); i++) {
            info.append("<p><font color='").append(getColorHex(i)).append("'>");
            info.append("Joueur ").append(i + 1).append(" (").append(game.getPlayers().get(i).getName()).append(")");
            info.append("</font></p>");
        }
        info.append("</body></html>");
        infoTextArea.setText(info.toString());
    }

    private void updateInfoDisplayGameStarted() {
        StringBuilder info = new StringBuilder();
        var currentPlayer = game.getCurrentPlayerIndex();
        Player currentPlayerObj = game.getPlayers().get(currentPlayer);
        specialButton.setVisible(!currentPlayerObj.isCapacityUsed() && currentAction != SubPlayerAction.SELECT_PAWN);
        info.append("<html><head><style>");
        info.append("body { font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 10px; }");
        info.append("h2 { color: #333; font-size: 16px; margin: 5px 0; }");
        info.append("h3 { color: #555; font-size: 14px; margin: 5px 0; }");
        info.append(".current-player { font-weight: bold; font-size: 14px; }");
        info.append(".action-message { background-color: #e8f4f8; padding: 8px; border-left: 3px solid #2196F3; margin: 10px 0; }");
        info.append("</style></head><body>");
        info.append("<h2>PHASE DE JEU : <font color='").append(getColorHex(currentPlayer)).append("'>");
        info.append(game.getPlayers().get(currentPlayer).getName());
        info.append("</font></h2>");
        info.append("<p>");
        info.append(getCurrentActionMessage()).append(".</p><br>");

        info.append("<h3>JOUEURS</h3>");
        for (int i = 0; i < game.getPlayers().size(); i++) {
            info.append("<p><font color='").append(getColorHex(i)).append("'>");
            info.append("<b>Joueur ").append(i + 1).append(" (").append(game.getPlayers().get(i).getName()).append(")</b><br>");
            //info.append("Score: ").append(playerScores[i]).append("<br>"); // Placeholder pour le score
            info.append("Capacité spéciale: ");
            info.append(game.getPlayers().get(i).isCapacityUsed() ? "Utilisée" : "Disponible");
            info.append("</font></p>");
        }
        info.append("</body></html>");

        infoTextArea.setText(info.toString());
    }

    private void setupBoardDisplay(){
        gamePanel.removeAll();
        gamePanel.setLayout(new BorderLayout());

        BoardPanel boardPanel = new BoardPanel();
        gamePanel.add(boardPanel, BorderLayout.CENTER);
        gamePanel.repaint();
    }

    private void handleSetupPhase(Position clickedPosition) {


        Player currentPlayer = game.getCurrentPlayer();

        // Trouver le premier pion non placé du joueur
        for (int i = 0; i < currentPlayer.getPawns().length; i++) {
            try {
                currentPlayer.getPawns()[i].getPosition();
                // Si on arrive ici, le pion est déjà placé
            } catch (IllegalStateException e) {
                // Pion non placé, on peut le placer
                if (game.playerPlacePawns(currentPlayer, i, clickedPosition)) {
                    game.nextPlayer();
                    if (game.isAllPawnArePlaced()) {
                        game.start();
                        currentAction = SubPlayerAction.SELECT_PAWN;
                    }
                    refreshWindow();
                }
                break;
            }
        }
    }

    private void handlePlayPhase(Position clickedPosition){
        Player currentPlayer = game.getCurrentPlayer();

        var cellClicked = game.getBoard().getCellAt(clickedPosition);

        switch (currentAction){
            case PLACE_PAWN -> {
                throw new IllegalStateException("Action non supportée en phase de jeu : PLACE_PAWN");
            }
            case SELECT_PAWN -> {
                if (cellClicked.isOccuped()){
                    Pawn pawnClicked;
                    try {
                        pawnClicked = cellClicked.getOptionalPawn().get();
                    } catch (NoSuchElementException e) {
                        throw new IllegalStateException("Aucun pion trouvé à la position cliquée: " + clickedPosition);
                    }

                    // Vérifier si le joueur a cliqué sur un de ses pions
                    if (pawnClicked.getPlayerId() == currentPlayer.getId()) {

                        // Verifier si le joueur peut jouer ce pion
                        if (game.getBoard().isPawnCanPlaceWall(pawnClicked)){
                            selectedPawn = pawnClicked;
                            nextSubAction();
                        };

                    }
                }
            }

            case MOVE_PAWN -> {
                if (!cellClicked.isOccuped()){
                    // calculer la distance de cette case par rapport au pion sélectionné
                    if (game.getBoard().isPawnCanMoveTo(selectedPawn, clickedPosition, currentMovedCells)) {
                        // Déplacer le pion à la nouvelle position
                        var optionalPawn = game.getBoard().removePawnAt(selectedPawn.getPosition());
                        if (optionalPawn.isEmpty()) {
                            throw new IllegalStateException("Aucun pion trouvé à la position sélectionnée: " + selectedPawn.getPosition());
                        }
                        currentMovedCells += selectedPawn.getPosition().distanceTo(clickedPosition);
                        if (!game.getBoard().placePawnAt(optionalPawn.get(), clickedPosition)){
                            throw new IllegalStateException("Impossible de placer le pion à la position cliquée: " + clickedPosition);
                        };
                        selectedPawn.setPosition(clickedPosition);
                        nextSubAction();
                    } else {
                        JOptionPane.showMessageDialog(this, "Déplacement impossible pour le pion sélectionné.", "Erreur de déplacement", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    if (cellClicked.getOptionalPawn().get().getPosition().equals(selectedPawn.getPosition())){
                        if (game.getBoard().isPawnCanPlaceWall(selectedPawn)) {
                            nextSubAction();
                        } else {
                            JOptionPane.showMessageDialog(this, "Impossible de placer un mur avec ce pion.", "Erreur de placement", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }

            case PLACE_WALL -> {
                //calculer le delta entre la position du pion sélectionné et la position cliquée
                var currentPosition = selectedPawn.getPosition();
                int deltaX = clickedPosition.getX() - currentPosition.getX();
                int deltaY = clickedPosition.getY() - currentPosition.getY();

                var canPlaceWall = false;
                if (deltaX > 0){
                    // Déplacement vers l'est
                    if (game.getBoard().placeWall(currentPosition, Direction.EAST)) {
                        canPlaceWall = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Impossible de placer le mur à l'est.", "Erreur de placement", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (deltaX < 0) {
                    // Déplacement vers l'ouest
                    if (game.getBoard().placeWall(currentPosition, Direction.WEST)) {
                        canPlaceWall = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Impossible de placer le mur à l'ouest.", "Erreur de placement", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (deltaY > 0) {
                    // Déplacement vers le sud
                    if (game.getBoard().placeWall(currentPosition, Direction.SOUTH)) {
                        canPlaceWall = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Impossible de placer le mur au sud.", "Erreur de placement", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (deltaY < 0) {
                    // Déplacement vers le nord
                    if (game.getBoard().placeWall(currentPosition, Direction.NORTH)) {
                        canPlaceWall = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Impossible de placer le mur au nord.", "Erreur de placement", JOptionPane.ERROR_MESSAGE);
                    }
                }

                if (canPlaceWall) {
                    nextSubAction();
                }

            }

            case DELETE_WALL -> {
                var currentPosition = selectedPawn.getPosition();
                int deltaX = clickedPosition.getX() - currentPosition.getX();
                int deltaY = clickedPosition.getY() - currentPosition.getY();

                var canRemoveWall = false;
                if (deltaX > 0){
                    // Déplacement vers l'est
                    if (game.getBoard().removeWall(currentPosition, Direction.EAST)) {
                        canRemoveWall = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Impossible de placer le mur à l'est.", "Erreur de placement", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (deltaX < 0) {
                    // Déplacement vers l'ouest
                    if (game.getBoard().removeWall(currentPosition, Direction.WEST)) {
                        canRemoveWall = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Impossible de placer le mur à l'ouest.", "Erreur de placement", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (deltaY > 0) {
                    // Déplacement vers le sud
                    if (game.getBoard().removeWall(currentPosition, Direction.SOUTH)) {
                        canRemoveWall = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Impossible de placer le mur au sud.", "Erreur de placement", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (deltaY < 0) {
                    // Déplacement vers le nord
                    if (game.getBoard().removeWall(currentPosition, Direction.NORTH)) {
                        canRemoveWall = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Impossible de placer le mur au nord.", "Erreur de placement", JOptionPane.ERROR_MESSAGE);
                    }
                }

                if (canRemoveWall) {
                    currentPlayer.useCapacity();
                }
                nextSubAction();
            }
        }
    }

    private void nextSubAction() {
        switch (currentAction) {
            case PLACE_PAWN:
                currentAction = SubPlayerAction.SELECT_PAWN;
                break;
            case SELECT_PAWN:
                currentAction = SubPlayerAction.MOVE_PAWN;
                break;
            case DELETE_WALL:
                if (currentMovedCells < 2){
                    currentAction = SubPlayerAction.MOVE_PAWN;
                } else {
                    currentAction = SubPlayerAction.PLACE_WALL;
                }
                break;
            case MOVE_PAWN:
                currentAction = SubPlayerAction.PLACE_WALL;
                System.out.println("déplacé de "+ currentMovedCells);
                break;
            case PLACE_WALL:
                currentAction = SubPlayerAction.SELECT_PAWN;
                selectedPawn = null;
                currentMovedCells = 0;
                game.nextPlayer();
                break;
        }
        refreshWindow();
    }

    private String getCurrentActionMessage() {
        switch (currentAction) {
            case SELECT_PAWN:
                return "Cliquez sur un de vos pions pour le sélectionner";
            case MOVE_PAWN:
                return "Cliquez sur une case pour déplacer le pion sélectionné";
            case PLACE_WALL:
                return "Cliquez pour placer un mur";
            case DELETE_WALL:
                return "Cliquez pour supprimer un mur";
            default:
                return "";
        }
    }

    private void useSpecialCapacity() {
        Player currentPlayer = game.getCurrentPlayer();
        if (!currentPlayer.isCapacityUsed()) {
            currentAction = SubPlayerAction.DELETE_WALL;
            System.out.println("Utilisation de la capacité spéciale du joueur " + currentPlayer.getName());
            refreshWindow();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            var playerNames = new ArrayList<String>(2);
            playerNames.add("Alice");
            playerNames.add("Bob");
            //playerNames.add("Charlie");
            //playerNames.add("Diana");
            var game = GameFactory.createGame(2, playerNames);
            var gameWindow = new GameWindow(game);
        });
    }
}