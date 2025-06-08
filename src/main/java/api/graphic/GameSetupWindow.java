package api.graphic;

import model.Game;
import util.GameFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class GameSetupWindow extends JFrame {
    
    private JSpinner playerCountSpinner;
    private JTextField[] playerNameFields;
    private JPanel playerNamesPanel;
    private JButton startGameButton;
    private JButton cancelButton;
    private final CompletableFuture<Game> gameCompletableFuture = new CompletableFuture<>();

    public GameSetupWindow() {
        initializeWindow();
        createComponents();
        setupEventListeners();
        setVisible(true);
    }
    public CompletableFuture<Game> getGameAsync() {
        return gameCompletableFuture;
    }

    private void setup(int numberOfPlayers, ArrayList<String> playerNames) {
        Objects.requireNonNull(playerNames, "playetNames cannot be null");
        if (numberOfPlayers < 2 || numberOfPlayers > 4) {
            throw new IllegalArgumentException("Le nombre de joueurs doit être entre 2 et 4.");
        }
        Game game = GameFactory.createGame(numberOfPlayers, playerNames);

        System.out.println("Partie configurée avec " + numberOfPlayers + " joueurs.");
        System.out.println("Noms des joueurs : " + Arrays.toString(playerNames.toArray()));
        gameCompletableFuture.complete(game);
    }

    private void initializeWindow() {
        setTitle("Quoridor - Configuration de la partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
    }
    
    private void createComponents() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titre
        JLabel titleLabel = new JLabel("Configuration de la partie", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Panel central pour les options
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // Sélection du nombre de joueurs
        JPanel playerCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerCountPanel.add(new JLabel("Nombre de joueurs :"));
        playerCountSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 4, 1));
        playerCountSpinner.setPreferredSize(new Dimension(60, 25));
        playerCountPanel.add(playerCountSpinner);
        
        centerPanel.add(playerCountPanel, BorderLayout.NORTH);
        
        // Panel pour les noms des joueurs
        playerNamesPanel = new JPanel();
        playerNamesPanel.setLayout(new BoxLayout(playerNamesPanel, BoxLayout.Y_AXIS));
        playerNamesPanel.setBorder(BorderFactory.createTitledBorder("Noms des joueurs"));
        
        // Initialiser avec 2 joueurs par défaut
        updatePlayerNameFields(2);
        
        JScrollPane scrollPane = new JScrollPane(playerNamesPanel);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        startGameButton = new JButton("Commencer la partie");
        cancelButton = new JButton("Annuler");
        
        startGameButton.setPreferredSize(new Dimension(150, 30));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(startGameButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupEventListeners() {
        // Changement du nombre de joueurs
        playerCountSpinner.addChangeListener(e -> {
            int playerCount = (Integer) playerCountSpinner.getValue();
            updatePlayerNameFields(playerCount);
        });
        
        // Bouton commencer
        startGameButton.addActionListener(e -> {
            if (validateInputs()) {
                int numberOfPlayers = (Integer) playerCountSpinner.getValue();
                ArrayList<String> playerNames = new ArrayList<>(numberOfPlayers);
                
                for (int i = 0; i < numberOfPlayers; i++) {
                    playerNames.add( playerNameFields[i].getText().trim());
                    if (playerNames.get(i).isEmpty()) {
                        playerNames.set(i, "Joueur " + (i + 1)); // Nom par défaut
                    }
                }
                setup(numberOfPlayers, playerNames);
                dispose();
            }
        });
        
        // Bouton annuler
        cancelButton.addActionListener(e -> {
            gameCompletableFuture.complete(null);
            System.out.println("Configuration annulée");
            dispose();
        });
    }
    
    private void updatePlayerNameFields(int playerCount) {
        playerNamesPanel.removeAll();
        playerNameFields = new JTextField[playerCount];
        
        String[] colors = {"BLEU", "ROUGE", "VERT", "JAUNE"};
        Color[] playerColors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
        
        for (int i = 0; i < playerCount; i++) {
            JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            
            JLabel playerLabel = new JLabel("Joueur " + (i + 1) + " (" + colors[i] + ") :");
            playerLabel.setForeground(playerColors[i]);
            playerLabel.setFont(new Font("Arial", Font.BOLD, 12));
            playerLabel.setPreferredSize(new Dimension(120, 25));
            
            playerNameFields[i] = new JTextField(15);
            playerNameFields[i].setText("Joueur " + (i + 1)); // Nom par défaut
            
            playerPanel.add(playerLabel);
            playerPanel.add(playerNameFields[i]);
            
            playerNamesPanel.add(playerPanel);
        }
        
        playerNamesPanel.revalidate();
        playerNamesPanel.repaint();
    }
    
    private boolean validateInputs() {
        int playerCount = (Integer) playerCountSpinner.getValue();
        
        // Vérifier les noms en double
        List<String> names = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            String name = playerNameFields[i].getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Erreur : Le nom du joueur " + (i + 1) + " ne peut pas être vide !",
                        "Nom vide",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (!name.isEmpty()) {
                if (names.contains(name)) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur : Deux joueurs ne peuvent pas avoir le même nom !", 
                        "Noms en double", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                names.add(name);
            }
        }
        
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new GameSetupWindow();
        });
    }
}