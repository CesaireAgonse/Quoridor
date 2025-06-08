package api.graphic;

import model.Game;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    
    public MainWindow() {
        initializeWindow();
        createButtons();
        setVisible(true);
    }
    
    private void initializeWindow() {
        setTitle("Quoridor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Centre la fenêtre
        setLayout(new BorderLayout());
    }
    
    private void createButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JButton newGameButton = new JButton("Nouvelle partie");
        JButton continueButton = new JButton("Continuer");
        JButton rulesButton = new JButton("Règles");

        // Enlever les effets de focus disgracieux
        newGameButton.setFocusPainted(false);
        continueButton.setFocusPainted(false);
        rulesButton.setFocusPainted(false);
        
        // Styliser les boutons
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        newGameButton.setFont(buttonFont);
        continueButton.setFont(buttonFont);
        rulesButton.setFont(buttonFont);
        
        newGameButton.setPreferredSize(new Dimension(200, 50));
        continueButton.setPreferredSize(new Dimension(200, 50));
        rulesButton.setPreferredSize(new Dimension(200, 50));
        
        buttonPanel.add(newGameButton);
        buttonPanel.add(continueButton);
        buttonPanel.add(rulesButton);
        
        add(buttonPanel, BorderLayout.CENTER);
        
        // Actions des boutons
        newGameButton.addActionListener(e -> startNewGame());
        continueButton.addActionListener(e -> continueGame());
        rulesButton.addActionListener(e -> showRules());
    }
    
    private void startNewGame() {
        // Fermer le menu principal
        this.dispose();

        GameSetupWindow gameSetupWindow = new GameSetupWindow();

        // Attendre de façon asynchrone
        gameSetupWindow.getGameAsync().thenAccept(gameCreated -> {
            SwingUtilities.invokeLater(() -> {
                if (gameCreated == null) {
                    JOptionPane.showMessageDialog(null, "Configuration annulée ou erreur.", "Information", JOptionPane.INFORMATION_MESSAGE);
                    new MainWindow(); // Retourner au menu principal
                } else {
                    JOptionPane.showMessageDialog(null, "Partie créée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    // Lancer la fenêtre de jeu
                    //new GameWindow(gameCreated);
                }
            });
        });
    }

    
    private void continueGame() {
        JOptionPane.showMessageDialog(this, "C'est dans les back...");
        // TODO: Implémenter la logique de continuation
    }
    
    private void showRules() {
        String rules = "Règles du Quoridor :\n\n" +
                      "Pour l'instant il faut check sur le readme\n" +
                      "Cliquez sur \"Nouvelle partie\" pour commencer !";
        
        JOptionPane.showMessageDialog(this, rules, "Règles du Go", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainWindow();
        });
    }
}