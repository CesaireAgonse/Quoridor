package api.graphic;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    
    private JPanel gamePanel;
    private JTextArea infoTextArea;
    
    public GameWindow() {
        initializeWindow();
        createGameArea();
        createInfoArea();
        setVisible(true);
    }
    
    private void initializeWindow() {
        setTitle("Quoridor - Partie en cours");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    
    private void createGameArea() {
        gamePanel = new JPanel();
        gamePanel.setBackground(Color.WHITE);
        //gamePanel.setBorder(BorderFactory.createTitledBorder("Zone de jeu"));
        
        // La zone de jeu occupe la majeure partie de la fenêtre
        add(gamePanel, BorderLayout.CENTER);
    }
    
    private void createInfoArea() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informations"));
        infoPanel.setPreferredSize(new Dimension(200, 0)); // Largeur fixe
        
        infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setBackground(Color.LIGHT_GRAY);
        infoTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        infoTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
    }
    
    public JPanel getGamePanel() {
        return gamePanel;
    }
    
    public JTextArea getInfoTextArea() {
        return infoTextArea;
    }
}