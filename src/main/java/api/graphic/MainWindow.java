package api.graphic;

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
        JOptionPane.showMessageDialog(this, "Nouvelle partie démarrée !");
        // Fermer le menu principal
        this.dispose();

        // Ouvrir la fenêtre de jeu
        new GameWindow();
    }
    
    private void continueGame() {
        JOptionPane.showMessageDialog(this, "Continuer la partie...");
        // TODO: Implémenter la logique de continuation
    }
    
    private void showRules() {
        String rules = "Règles du Go :\n\n" +
                      "1. Les joueurs placent alternativement des pierres sur les intersections\n" +
                      "2. Le but est de contrôler le plus de territoire possible\n" +
                      "3. Les pierres entourées sont capturées\n" +
                      "4. La partie se termine quand les deux joueurs passent\n\n" +
                      "Cliquez sur 'Nouvelle partie' pour commencer !";
        
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