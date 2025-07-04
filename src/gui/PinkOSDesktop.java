package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PinkOSDesktop extends JFrame {
    private JDesktopPane desktop;
    private JPanel taskbar;
    private JLabel clockLabel;
    private Timer clockTimer;
    private ImageIcon backgroundImage;
    private Font montserratRegular;
    private Font montserratBold;
    
    public PinkOSDesktop() {
        loadFonts();
        initializeDesktop();
        createTaskbar();
        createDesktopIcons();
        startClock();
        setVisible(true);
    }
    
    private void loadFonts() {
        // Try to load Montserrat font if available, otherwise fall back to system fonts
        try {
            // Check if Montserrat is already available in the system
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = ge.getAvailableFontFamilyNames();
            
            boolean montserratAvailable = false;
            for (String fontName : fontNames) {
                if (fontName.equals("Montserrat")) {
                    montserratAvailable = true;
                    break;
                }
            }
            
            if (montserratAvailable) {
                montserratRegular = new Font("Montserrat", Font.PLAIN, 14);
                montserratBold = new Font("Montserrat", Font.BOLD, 14);
            } else {
                // Fall back to a nice sans-serif font
                montserratRegular = new Font("Segoe UI", Font.PLAIN, 14);
                montserratBold = new Font("Segoe UI", Font.BOLD, 14);
            }
        } catch (Exception e) {
            // Fallback to default fonts if any error occurs
            montserratRegular = new Font("SansSerif", Font.PLAIN, 14);
            montserratBold = new Font("SansSerif", Font.BOLD, 14);
        }
    }
    
    private void initializeDesktop() {
        setTitle("PinkOS - A Wholesome Operating System ♥");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Load the background image
        try {
            backgroundImage = new ImageIcon("d:/JavaProject_OS/PinkOS/resources/bg2.jpg");
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            backgroundImage = null; // Will fall back to gradient if null
        }
        
        // Create desktop pane with image background
        desktop = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                if (backgroundImage != null && backgroundImage.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    // Draw the image, scaled to fit the desktop
                    g2d.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fall back to gradient background if image can't be loaded
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 192, 203), 
                                                              0, getHeight(), new Color(255, 228, 225));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        setLayout(new BorderLayout());
        add(desktop, BorderLayout.CENTER);
    }
    
    private void createTaskbar() {
        taskbar = new JPanel(new BorderLayout());
        taskbar.setBackground(new Color(255, 182, 193));
        taskbar.setBorder(BorderFactory.createRaisedBevelBorder());
        taskbar.setPreferredSize(new Dimension(0, 50)); // Increased height
        
        // Start menu button
        JButton startButton = new JButton("♥ Start");
        startButton.setFont(montserratBold.deriveFont(16f));
        startButton.setBackground(new Color(255, 105, 180));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15))); // Added padding
        startButton.addActionListener(e -> showStartMenu());
        
        // Clock
        clockLabel = new JLabel();
        clockLabel.setForeground(new Color(139, 69, 19));
        clockLabel.setFont(montserratBold.deriveFont(16f));
        clockLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Added padding
        
        taskbar.add(startButton, BorderLayout.WEST);
        taskbar.add(clockLabel, BorderLayout.EAST);
        
        add(taskbar, BorderLayout.SOUTH);
    }
    
    private void createDesktopIcons() {
        // Notepad icon with improved design
        JButton notepadIcon = createDesktopIcon("✎\nNotepad", 60, 60);
        notepadIcon.addActionListener(e -> openNotepad());
        desktop.add(notepadIcon);
        
        // Calculator icon with improved design
        JButton calcIcon = createDesktopIcon("≡\nCalculator", 60, 180);
        calcIcon.addActionListener(e -> openCalculator());
        desktop.add(calcIcon);
        
        // File Explorer icon with improved design
        JButton fileIcon = createDesktopIcon("⧉\nFiles", 60, 300);
        fileIcon.addActionListener(e -> openFileExplorer());
        desktop.add(fileIcon);
        
        // Games icon with improved design
        JButton gamesIcon = createDesktopIcon("♟\nGames", 60, 420);
        gamesIcon.addActionListener(e -> openGames());
        desktop.add(gamesIcon);
        
        // Music Player icon with improved design
        JButton musicIcon = createDesktopIcon("♫\nMusic", 60, 540);
        musicIcon.addActionListener(e -> openMusicPlayer());
        desktop.add(musicIcon);
    }
    
    private JButton createDesktopIcon(String text, int x, int y) {
        JButton icon = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        icon.setBounds(x, y, 100, 100); // Increased size
        
        // Improved icon style
        icon.setBackground(new Color(255, 240, 245, 200)); // Added transparency
        icon.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 105, 180, 150), 2, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        icon.setFocusPainted(false);
        icon.setFont(montserratBold.deriveFont(14f));
        icon.setForeground(new Color(139, 69, 19));
        
        // Add hover effect
        icon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                icon.setBackground(new Color(255, 182, 193, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                icon.setBackground(new Color(255, 240, 245, 200));
            }
        });
        
        return icon;
    }
    
    private void startClock() {
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();
    }
    
    private void updateClock() {
        LocalTime now = LocalTime.now();
        clockLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " ⌚");
    }
    
    private void showStartMenu() {
        JPopupMenu startMenu = new JPopupMenu();
        
        // Apply custom style to popup menu
        startMenu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 105, 180), 2),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        startMenu.setBackground(new Color(255, 240, 245));
        
        // Add menu items with improved styling
        startMenu.add(createMenuItem("✎ Notepad", e -> openNotepad()));
        startMenu.add(createMenuItem("≡ Calculator", e -> openCalculator()));
        startMenu.add(createMenuItem("⧉ Explorer", e -> openFileExplorer()));
        startMenu.add(createMenuItem("♟ Games", e -> openGames()));
        startMenu.add(createMenuItem("♫ Music Player", e -> openMusicPlayer()));
        startMenu.addSeparator();
        startMenu.add(createMenuItem("✰ Sleep", e -> JOptionPane.showMessageDialog(this, "Sweet dreams!")));
        startMenu.add(createMenuItem("↻ Restart", e -> JOptionPane.showMessageDialog(this, "Restarting...")));
        startMenu.add(createMenuItem("⊗ Shutdown", e -> System.exit(0)));
        
        // Calculate position - display above taskbar with increased width
        Dimension menuSize = startMenu.getPreferredSize();
        menuSize.width = Math.max(menuSize.width, 220); // Ensure minimum width
        startMenu.setPopupSize(menuSize);
        
        startMenu.show(taskbar, 0, -menuSize.height);
    }
    
    private JMenuItem createMenuItem(String text, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(montserratRegular.deriveFont(16f));
        item.setForeground(new Color(139, 69, 19));
        item.setBackground(new Color(255, 240, 245));
        
        // Add padding to menu items
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Add hover effect
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(255, 182, 193));
                item.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(255, 240, 245));
                item.setForeground(new Color(139, 69, 19));
            }
        });
        
        item.addActionListener(action);
        return item;
    }
    
    private void openNotepad() {
        SimpleNotepad notepad = new SimpleNotepad();
        desktop.add(notepad);
        notepad.setVisible(true);
    }
    
    private void openCalculator() {
        SimpleCalculator calc = new SimpleCalculator();
        desktop.add(calc);
        calc.setVisible(true);
    }
    
    private void openFileExplorer() {
        SimpleFileExplorer explorer = new SimpleFileExplorer();
        desktop.add(explorer);
        explorer.setVisible(true);
    }
    
    private void openGames() {
        SimpleGames games = new SimpleGames();
        desktop.add(games);
        games.setVisible(true);
    }
    
    private void openMusicPlayer() {
        SimpleMusicPlayer player = new SimpleMusicPlayer();
        desktop.add(player);
        player.setVisible(true);
    }
}
