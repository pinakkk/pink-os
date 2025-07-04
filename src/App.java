import gui.PinkOSDesktop;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Arrays;

public class App {
    public static void main(String[] args) throws Exception {
        // When i run this app.java, the operating system gui should run.
        showBootScreen();
    }
    
    private static void showBootScreen() {
        // Create boot splash screen
        JFrame bootScreen = new JFrame("PinkOS Booting");
        bootScreen.setUndecorated(true);
        bootScreen.setSize(500, 300);
        bootScreen.setLocationRelativeTo(null);
        bootScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 192, 203), 
                    0, getHeight(), new Color(255, 228, 225)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Title label
        JLabel titleLabel = new JLabel("PinkOS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(new Color(139, 69, 19));
        
        // Status message
        JLabel statusLabel = new JLabel("Initializing system...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel.setForeground(new Color(139, 69, 19));
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setForeground(new Color(255, 105, 180));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(255, 240, 245));
        
        // Boot screen layout
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(statusLabel, BorderLayout.CENTER);
        contentPanel.add(progressBar, BorderLayout.SOUTH);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        bootScreen.add(mainPanel);
        bootScreen.setVisible(true);
        
        // Boot messages to display
        List<String> bootMessages = Arrays.asList(
            "Initializing system components...",
            "Loading core services...",
            "Preparing file system...",
            "Configuring network...",
            "Starting GUI subsystem...",
            "Loading desktop environment...",
            "PinkOS is ready!"
        );
        
        // Simulate boot process in a separate thread
        new Thread(() -> {
            try {
                // Simulate booting process
                for (int i = 0; i <= 100; i++) {
                    final int progress = i;
                    
                    // Update UI on EDT
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(progress);
                        
                        // Update status message at certain intervals
                        if (progress > 0) {
                            int messageIndex = (progress * bootMessages.size()) / 100;
                            if (messageIndex >= bootMessages.size()) {
                                messageIndex = bootMessages.size() - 1;
                            }
                            statusLabel.setText(bootMessages.get(messageIndex));
                        }
                    });
                    
                    // Sleep to simulate processing time
                    Thread.sleep(50);
                }
                
                // Boot complete - launch the desktop
                Thread.sleep(500); // Brief pause at 100%
                bootScreen.dispose(); // Close boot screen
                
                // Launch the actual desktop
                SwingUtilities.invokeLater(() -> {
                    new PinkOSDesktop();
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
