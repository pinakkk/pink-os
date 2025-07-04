package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

public class SimpleMusicPlayer extends JInternalFrame {
    // UI components
    private JPanel mainPanel;
    private JList<String> playlistList;
    private DefaultListModel<String> playlistModel;
    private JLabel nowPlayingLabel;
    private JLabel artistLabel;
    private JLabel timeLabel;
    private JSlider progressSlider;
    private JSlider volumeSlider;
    private JButton playButton;
    private JButton pauseButton;
    private JButton nextButton;
    private JButton prevButton;
    private JPanel albumArtPanel;
    private Timer updateTimer;
    
    // Audio playback components
    private Clip audioClip;
    private boolean isPlaying = false;
    private List<File> songFiles = new ArrayList<>();
    private int currentSongIndex = 0;
    private float currentVolume = 0.7f;
    
    // Path to resources directory
    private final String RESOURCES_PATH = "d:/JavaProject_OS/PinkOS/resources/music/";
    
    public SimpleMusicPlayer() {
        super("♫ Pink Music Player", true, true, true, true);
        initializeMusicPlayer();
        loadSongsFromResources(); // Load songs automatically
        
        // Add internal frame listener to handle closing
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                cleanup();
            }
        });
    }
    
    /**
     * Clean up resources when closing
     */
    private void cleanup() {
        // Stop playback and release audio resources
        if (audioClip != null) {
            audioClip.stop();
            audioClip.close();
            audioClip = null;
        }
        
        // Stop any timers
        if (updateTimer != null) {
            updateTimer.stop();
            updateTimer = null;
        }
    }
    
    @Override
    public void dispose() {
        cleanup();
        super.dispose();
    }
    
    private void initializeMusicPlayer() {
        setSize(600, 450);
        setLocation(200, 150);
        
        // Create main panel with dark background (Spotify-like)
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(18, 18, 18)); // Spotify dark background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create the three main sections
        JPanel leftPanel = createLeftPanel();   // Album art and current song info
        JPanel centerPanel = createCenterPanel(); // Playlist
        JPanel bottomPanel = createControlPanel(); // Controls
        
        // Add panels to main layout
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Menu bar with just title/info - no file chooser
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(40, 40, 40));
        menuBar.setBorder(BorderFactory.createEmptyBorder());
        
        JMenu aboutMenu = new JMenu("About");
        aboutMenu.setForeground(Color.WHITE);
        
        JMenuItem infoItem = new JMenuItem("Music Player Info");
        infoItem.addActionListener(e -> showAboutDialog());
        aboutMenu.add(infoItem);
        
        menuBar.add(aboutMenu);
        setJMenuBar(menuBar);
        
        // Initialize timer for updating progress
        updateTimer = new Timer(500, e -> updateProgress());
    }
    
    /**
     * Load songs from resources directory
     */
    private void loadSongsFromResources() {
        // Create the music directory if it doesn't exist
        File musicDir = new File(RESOURCES_PATH);
        if (!musicDir.exists()) {
            musicDir.mkdirs();
            JOptionPane.showMessageDialog(this,
                "Created music directory at:\n" + RESOURCES_PATH + 
                "\nPlease add .wav audio files to this folder.",
                "Music Directory Created",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Scan directory for supported audio files
        File[] files = musicDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".wav") || 
            name.toLowerCase().endsWith(".au") || 
            name.toLowerCase().endsWith(".aiff"));
            
        // Handle case when no audio files are found
        if (files == null || files.length == 0) {
            artistLabel.setText("No audio files found in resources");
            return;
        }
        
        // Add all found files to the playlist
        for (File file : files) {
            try {
                // Verify it's an audio file we can play
                AudioSystem.getAudioInputStream(file);
                
                // Add to our list
                songFiles.add(file);
                playlistModel.addElement(file.getName());
                
            } catch (Exception e) {
                System.out.println("Skipping file: " + file.getName() + 
                                  " - Not a supported audio format");
            }
        }
        
        // Select and load the first song if any were found
        if (!songFiles.isEmpty()) {
            playlistList.setSelectedIndex(0);
            currentSongIndex = 0;
            loadSong(songFiles.get(0));
            artistLabel.setText("PinkOS Music Collection");
        }
    }
    
    private JPanel createLeftPanel() {
        // Same as before...
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(18, 18, 18));
        panel.setPreferredSize(new Dimension(200, 0));
        
        // Album art panel (shows cover or visualization)
        albumArtPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawAlbumArt(g);
            }
        };
        albumArtPanel.setPreferredSize(new Dimension(180, 180));
        albumArtPanel.setBackground(new Color(40, 40, 40));
        albumArtPanel.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 30), 1));
        
        // Song information panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        infoPanel.setBackground(new Color(18, 18, 18));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        nowPlayingLabel = new JLabel("No song playing");
        nowPlayingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nowPlayingLabel.setForeground(Color.WHITE);
        
        artistLabel = new JLabel("Loading songs from resources...");
        artistLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        artistLabel.setForeground(new Color(179, 179, 179));
        
        infoPanel.add(nowPlayingLabel);
        infoPanel.add(artistLabel);
        
        panel.add(albumArtPanel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        // Same as before...
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(18, 18, 18));
        
        // Playlist header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 24, 24));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel playlistLabel = new JLabel("PINK COLLECTION");
        playlistLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        playlistLabel.setForeground(Color.WHITE);
        
        headerPanel.add(playlistLabel, BorderLayout.WEST);
        
        // Playlist content
        playlistModel = new DefaultListModel<>();
        playlistList = new JList<>(playlistModel);
        playlistList.setBackground(new Color(24, 24, 24));
        playlistList.setForeground(Color.WHITE);
        playlistList.setSelectionBackground(new Color(80, 80, 80));
        playlistList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        playlistList.setCellRenderer(new SongCellRenderer());
        playlistList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && playlistList.getSelectedIndex() >= 0) {
                currentSongIndex = playlistList.getSelectedIndex();
                loadAndPlaySong(songFiles.get(currentSongIndex));
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(playlistList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 10));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        
        // Time and progress
        JPanel progressPanel = new JPanel(new BorderLayout(5, 0));
        progressPanel.setBackground(new Color(40, 40, 40));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        timeLabel = new JLabel("0:00 / 0:00");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(new Color(179, 179, 179));
        
        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(new Color(40, 40, 40));
        progressSlider.setForeground(new Color(255, 105, 180)); // Pink accent
        progressSlider.setUI(new SpotifySliderUI(progressSlider));
        progressSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (audioClip != null && audioClip.isOpen()) {
                    updateTimer.stop();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (audioClip != null && audioClip.isOpen()) {
                    int newPosition = progressSlider.getValue() * audioClip.getMicrosecondLength() / 100;
                    audioClip.setMicrosecondPosition(newPosition);
                    if (isPlaying) updateTimer.start();
                }
            }
        });
        
        progressPanel.add(progressSlider, BorderLayout.CENTER);
        progressPanel.add(timeLabel, BorderLayout.EAST);
        
        // Playback controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        controlsPanel.setBackground(new Color(40, 40, 40));
        
        // Use symbols instead of text for better usability
        prevButton = createControlButton("Previous");
        playButton = createControlButton("Play");
        pauseButton = createControlButton("Pause");
        nextButton = createControlButton("Next");
        
        controlsPanel.add(prevButton);
        controlsPanel.add(playButton);
        controlsPanel.add(pauseButton);
        controlsPanel.add(nextButton);
        
        // Volume control
        JPanel volumePanel = new JPanel(new BorderLayout(5, 0));
        volumePanel.setBackground(new Color(40, 40, 40));
        
        JLabel volumeIcon = new JLabel("🔊");
        volumeIcon.setForeground(Color.WHITE);
        
        volumeSlider = new JSlider(0, 100, (int)(currentVolume * 100));
        volumeSlider.setBackground(new Color(40, 40, 40));
        volumeSlider.setPreferredSize(new Dimension(100, 20));
        volumeSlider.setUI(new SpotifySliderUI(volumeSlider));
        volumeSlider.addChangeListener(e -> {
            currentVolume = volumeSlider.getValue() / 100f;
            updateVolume();
        });
        
        volumePanel.add(volumeIcon, BorderLayout.WEST);
        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        
        // Add action listeners for control buttons
        playButton.addActionListener(e -> play());
        pauseButton.addActionListener(e -> pause());
        nextButton.addActionListener(e -> next());
        prevButton.addActionListener(e -> previous());
        
        JPanel bottomControlPanel = new JPanel(new BorderLayout());
        bottomControlPanel.setBackground(new Color(40, 40, 40));
        bottomControlPanel.add(controlsPanel, BorderLayout.CENTER);
        bottomControlPanel.add(volumePanel, BorderLayout.EAST);
        
        panel.add(progressPanel, BorderLayout.NORTH);
        panel.add(bottomControlPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createControlButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(40, 40, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(255, 105, 180)); // Pink accent
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    // Drawing methods remain the same
    private void drawAlbumArt(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = albumArtPanel.getWidth();
        int height = albumArtPanel.getHeight();
        
        // Draw album art placeholder with music note
        g2d.setColor(new Color(60, 60, 60));
        g2d.fillRect(0, 0, width, height);
        
        if (isPlaying) {
            // Draw equalizer bars
            g2d.setColor(new Color(255, 105, 180)); // Pink accent
            
            // Number of bars and spacing
            int barCount = 5;
            int barWidth = 8;
            int spacing = 4;
            int totalWidth = barCount * barWidth + (barCount - 1) * spacing;
            int startX = (width - totalWidth) / 2;
            int baseY = height / 2 + 30;
            
            for (int i = 0; i < barCount; i++) {
                // Calculate height based on a sine wave to simulate animation
                int offset = (int)(System.currentTimeMillis() / 100) % 20;
                int barHeight = 15 + (int)(Math.sin((i + offset) * 0.5) * 15);
                
                g2d.fillRect(startX + i * (barWidth + spacing), 
                             baseY - barHeight, 
                             barWidth, 
                             barHeight);
            }
        }
        
        // Draw music note symbol
        g2d.setColor(new Color(200, 200, 200));
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String note = "♫";
        int textWidth = fm.stringWidth(note);
        g2d.drawString(note, (width - textWidth) / 2, height / 2);
    }
    
    private void loadSong(File file) {
        try {
            // Clean up existing audio
            if (audioClip != null) {
                audioClip.stop();
                audioClip.close();
                audioClip = null; // Set to null to ensure we create a new one
            }
            
            // Open the audio file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            
            // Set up clip listener for completion
            audioClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && 
                    audioClip.getMicrosecondPosition() >= audioClip.getMicrosecondLength()) {
                    SwingUtilities.invokeLater(() -> next());
                }
            });
            
            // Update volume
            updateVolume();
            
            // Update display
            nowPlayingLabel.setText(file.getName());
            albumArtPanel.repaint();
            
        } catch (Exception e) {
            System.err.println("Error loading audio: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(this,
                "Error loading audio file: " + e.getMessage(),
                "Playback Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAndPlaySong(File file) {
        loadSong(file);
        // Use invokeLater to ensure UI updates before playing
        SwingUtilities.invokeLater(() -> {
            play();
        });
    }
    
    private void play() {
        if (songFiles.isEmpty()) {
            System.out.println("No songs to play");
            return;
        }
        
        // If no audio clip or it's not properly initialized, load the current song
        if (audioClip == null || !audioClip.isOpen()) {
            System.out.println("Loading song because clip is null or not open");
            loadSong(songFiles.get(currentSongIndex));
            
            // Exit if loading failed
            if (audioClip == null) {
                System.err.println("Failed to load audio clip");
                return;
            }
        }
        
        // Check if we need to reset position
        if (audioClip.getMicrosecondPosition() >= audioClip.getMicrosecondLength()) {
            System.out.println("Resetting position to start");
            audioClip.setMicrosecondPosition(0);
        }
        
        // Now play if not already playing
        if (!isPlaying) {
            System.out.println("Starting playback");
            audioClip.start();
            isPlaying = true;
            updateTimer.start();
            albumArtPanel.repaint();
        } else {
            System.out.println("Already playing");
        }
    }
    
    private void pause() {
        if (audioClip != null && isPlaying) {
            audioClip.stop();
            isPlaying = false;
            updateTimer.stop();
            albumArtPanel.repaint();
        }
    }
    
    private void stop() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip.setMicrosecondPosition(0);
            isPlaying = false;
            updateTimer.stop();
            progressSlider.setValue(0);
            albumArtPanel.repaint();
        }
    }
    
    private void next() {
        if (songFiles.isEmpty()) return;
        
        stop();
        currentSongIndex = (currentSongIndex + 1) % songFiles.size();
        playlistList.setSelectedIndex(currentSongIndex);
        loadAndPlaySong(songFiles.get(currentSongIndex));
    }
    
    private void previous() {
        if (songFiles.isEmpty()) return;
        
        stop();
        currentSongIndex = (currentSongIndex - 1 + songFiles.size()) % songFiles.size();
        playlistList.setSelectedIndex(currentSongIndex);
        loadAndPlaySong(songFiles.get(currentSongIndex));
    }
    
    private void updateProgress() {
        if (audioClip != null && audioClip.isOpen()) {
            long position = audioClip.getMicrosecondPosition();
            long length = audioClip.getMicrosecondLength();
            
            // Update slider
            if (length > 0) {
                int progress = (int)((position * 100) / length);
                progressSlider.setValue(progress);
            }
            
            // Update time label
            String currentTime = formatTime(position / 1000000);
            String totalTime = formatTime(length / 1000000);
            timeLabel.setText(currentTime + " / " + totalTime);
        }
    }
    
    private void updateVolume() {
        if (audioClip != null && audioClip.isOpen()) {
            try {
                FloatControl volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
                // Convert linear scale to logarithmic gain (-80dB to 0dB)
                float gain = 20f * (float) Math.log10(currentVolume);
                // Limit to the control's range
                gain = Math.max(volumeControl.getMinimum(), Math.min(volumeControl.getMaximum(), gain));
                volumeControl.setValue(gain);
            } catch (Exception e) {
                System.out.println("Could not set volume: " + e.getMessage());
            }
        }
    }
    
    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "PinkOS Music Player\n\n" +
            "Playing music from: " + RESOURCES_PATH + "\n\n" +
            "Supported formats: WAV, AU, AIFF\n\n" +
            "Created with ♥ for PinkOS",
            "About Music Player",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Custom cell renderer and UI classes remain the same...
    private class SongCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            // Add music note icon
            if (index == currentSongIndex && isPlaying) {
                label.setText("▶ " + label.getText());
            }
            
            return label;
        }
    }
    
    private class SpotifySliderUI extends javax.swing.plaf.basic.BasicSliderUI {
        private Color trackColor = new Color(80, 80, 80);
        private Color activeTrackColor = new Color(255, 105, 180); // Pink accent
        private Color thumbColor = Color.WHITE;
        
        public SpotifySliderUI(JSlider slider) {
            super(slider);
        }
        
        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = trackRect.width;
            int height = Math.max(2, trackRect.height);
            int y = trackRect.y + (trackRect.height - height) / 2;
            
            // Background track
            g2d.setColor(trackColor);
            g2d.fillRoundRect(trackRect.x, y, width, height, height, height);
            
            // Active part of the track
            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                int thumbPos = thumbRect.x + thumbRect.width / 2;
                g2d.setColor(activeTrackColor);
                g2d.fillRoundRect(trackRect.x, y, thumbPos - trackRect.x, height, height, height);
            }
        }
        
        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(thumbColor);
            int size = 12;
            g2d.fillOval(thumbRect.x + (thumbRect.width - size) / 2, 
                         thumbRect.y + (thumbRect.height - size) / 2, 
                         size, size);
        }
        
        @Override
        protected Dimension getThumbSize() {
            return new Dimension(16, 16);
        }
    }
    
    private class CustomScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(new Color(24, 24, 24));
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(100, 100, 100));
            g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            g2.dispose();
        }
    }
}