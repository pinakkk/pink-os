package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SimpleGames extends JInternalFrame {
    
    public SimpleGames() {
        super("🎮 Pink Games", true, true, true, true);
        initializeGames();
    }
    
    private void initializeGames() {
        setSize(350, 250);
        setLocation(250, 250);
        
        JPanel gamePanel = new JPanel(new GridLayout(3, 1, 10, 10));
        gamePanel.setBackground(new Color(255, 240, 245));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton ticTacToeButton = createGameButton("🎯 Tic Tac Toe");
        ticTacToeButton.addActionListener(e -> playTicTacToe());
        
        JButton memoryButton = createGameButton("🧠 Memory Game");
        memoryButton.addActionListener(e -> playMemoryGame());
        
        JButton puzzleButton = createGameButton("🧩 Number Puzzle");
        puzzleButton.addActionListener(e -> playPuzzle());
        
        gamePanel.add(ticTacToeButton);
        gamePanel.add(memoryButton);
        gamePanel.add(puzzleButton);
        
        add(gamePanel);
        
        getContentPane().setBackground(new Color(255, 240, 245));
    }
    
    private JButton createGameButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(255, 182, 193));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }
    
    private void playTicTacToe() {
        JOptionPane.showMessageDialog(this, "🎯 Tic Tac Toe coming soon!\nStay tuned for fun! 🌸");
    }
    
    private void playMemoryGame() {
        JOptionPane.showMessageDialog(this, "🧠 Memory Game coming soon!\nExercise your brain! 💪");
    }
    
    private void playPuzzle() {
        JOptionPane.showMessageDialog(this, "🧩 Number Puzzle coming soon!\nChallenge yourself! ⭐");
    }
}
