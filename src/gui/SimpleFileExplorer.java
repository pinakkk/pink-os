// Version 1.0

// package gui;

// import javax.swing.*;
// import java.awt.*;
// import java.io.File;

// public class SimpleFileExplorer extends JInternalFrame {
//     private JList<String> fileList;
//     private DefaultListModel<String> listModel;
    
//     public SimpleFileExplorer() {
//         super("📁 Pink File Explorer", true, true, true, true);
//         initializeExplorer();
//     }
    
//     private void initializeExplorer() {
//         setSize(500, 500);
//         setLocation(400, 200);
        
//         listModel = new DefaultListModel<>();
//         fileList = new JList<>(listModel);
//         fileList.setBackground(new Color(255, 250, 250));
//         fileList.setSelectionBackground(new Color(255, 182, 193));
        
//         loadFiles();
        
//         JScrollPane scrollPane = new JScrollPane(fileList);
        
//         JLabel headerLabel = new JLabel("📁 Your Files", SwingConstants.CENTER);
//         headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
//         headerLabel.setForeground(new Color(139, 69, 19));
//         headerLabel.setBackground(new Color(255, 240, 245));
//         headerLabel.setOpaque(true);
        
//         add(headerLabel, BorderLayout.NORTH);
//         add(scrollPane, BorderLayout.CENTER);
        
//         getContentPane().setBackground(new Color(255, 240, 245));
//     }
    
//     private void loadFiles() {
//         listModel.clear();
//         listModel.addElement("🏠 Home");
//         listModel.addElement("📄 Documents");
//         listModel.addElement("🖼️ Pictures");
//         listModel.addElement("🎵 Music");
//         listModel.addElement("🎬 Videos");
//         listModel.addElement("📥 Downloads");
//         listModel.addElement("🗑️ Trash");
//         listModel.addElement("💖 Favorites");
//     }
// }

package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class SimpleFileExplorer extends JInternalFrame {
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JPanel contentPanel;
    private JTextArea fileContentArea;
    private JPanel headerPanel;
    private JLabel locationLabel;
    private JButton backButton;
    
    // View states
    private enum ViewType {
        MAIN_MENU, 
        FOLDER_VIEW,
        FILE_VIEW
    }
    
    private ViewType currentView = ViewType.MAIN_MENU;
    private String currentFolder = "";
    
    // Store file contents for secret messages
    private Map<String, String> secretMessages = new HashMap<>();
    
    public SimpleFileExplorer() {
        super("⧉ Pink File Explorer", true, true, true, true);
        initializeExplorer();
        loadSecretMessages();
    }
    
    private void initializeExplorer() {
        setSize(500, 400);
        setLocation(200, 200);
        
        // Main content panel with BorderLayout for proper resizing
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 240, 245));
        
        // Header panel for navigation
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 182, 193));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        // Location label
        locationLabel = new JLabel("File Explorer", SwingConstants.CENTER);
        locationLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        locationLabel.setForeground(new Color(139, 69, 19));
        
        // Back button
        backButton = new JButton("◀ Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backButton.setBackground(new Color(255, 105, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        backButton.setVisible(false); // Initially hidden
        backButton.addActionListener(e -> navigateBack());
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(locationLabel, BorderLayout.CENTER);
        
        // File list
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setBackground(new Color(255, 250, 250));
        fileList.setSelectionBackground(new Color(255, 182, 193));
        fileList.setSelectionForeground(Color.WHITE);
        fileList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        fileList.setCellRenderer(new FileListCellRenderer());
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleListItemClick();
                }
            }
        });
        
        JScrollPane listScrollPane = new JScrollPane(fileList);
        
        // File content area for viewing text files
        fileContentArea = new JTextArea();
        fileContentArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        fileContentArea.setBackground(new Color(255, 250, 250));
        fileContentArea.setForeground(new Color(139, 69, 19));
        fileContentArea.setLineWrap(true);
        fileContentArea.setWrapStyleWord(true);
        fileContentArea.setEditable(false);
        fileContentArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane contentScrollPane = new JScrollPane(fileContentArea);
        contentScrollPane.setVisible(false);
        
        // Add components to main panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(listScrollPane, BorderLayout.CENTER);
        
        setContentPane(contentPanel);
        
        // Load the main menu
        loadMainMenu();
    }
    
    private void loadSecretMessages() {
        // Create some wholesome messages
        secretMessages.put("daily_motivation.txt", 
            "Dear friend,\n\n" +
            "Today is a beautiful day, and YOU are a beautiful person!\n" +
            "Remember that you are enough, just as you are.\n" +
            "Your kindness makes the world a better place.\n\n" +
            "Keep shining! ♥");
        
        secretMessages.put("self_care_reminder.txt", 
            "Self-Care Reminders:\n\n" +
            "1. Drink some water\n" +
            "2. Take a deep breath\n" +
            "3. Stretch your body\n" +
            "4. Rest if you need to\n" +
            "5. You're doing great!\n\n" +
            "Be gentle with yourself today. ♥");
        
        secretMessages.put("gratitude_list.txt", 
            "Things to be grateful for:\n\n" +
            "1. The warmth of sunshine\n" +
            "2. The taste of your favorite food\n" +
            "3. The laughter of loved ones\n" +
            "4. Small moments of peace\n" +
            "5. The ability to grow and learn\n\n" +
            "What would you add to this list? ♥");
        
        secretMessages.put("friendly_note.txt", 
            "Hey there!\n\n" +
            "Just a friendly reminder that you're amazing!\n" +
            "Your journey is uniquely yours, and you're doing great.\n" +
            "Be proud of how far you've come.\n\n" +
            "Sending you good vibes! ♥");
    }
    
    private void loadMainMenu() {
        currentView = ViewType.MAIN_MENU;
        currentFolder = "";
        backButton.setVisible(false);
        locationLabel.setText("File Explorer");
        
        // Clear and add new items
        listModel.clear();
        listModel.addElement("✉ Secret Messages");
        listModel.addElement("☆ Photos");
        listModel.addElement("♫ Music");
        listModel.addElement("♥ Videos");
        listModel.addElement("★ Favourites");
        
        // Hide file content view if visible
        showFileContentView(false);
    }
    
    private void loadFolderView(String folder) {
        currentView = ViewType.FOLDER_VIEW;
        currentFolder = folder;
        backButton.setVisible(true);
        locationLabel.setText(folder);
        
        listModel.clear();
        
        if (folder.equals("✉ Secret Messages")) {
            for (String filename : secretMessages.keySet()) {
                listModel.addElement(filename);
            }
        } else if (folder.equals("♫ Music")) {
            listModel.addElement("Pink Sunshine.mp3");
            listModel.addElement("Heartbeats.mp3");
            listModel.addElement("Summer Vibes.mp3");
            listModel.addElement("Gentle Rain.mp3");
        } else if (folder.equals("☆ Photos")) {
            listModel.addElement("Beach Sunset.jpg");
            listModel.addElement("Pink Flowers.jpg");
            listModel.addElement("Mountain View.jpg");
            listModel.addElement("Cute Kitten.jpg");
        } else if (folder.equals("♥ Videos")) {
            listModel.addElement("Nature Documentary.mp4");
            listModel.addElement("Cooking Tutorial.mp4");
            listModel.addElement("Travel Memories.mp4");
        } else if (folder.equals("★ Favourites")) {
            listModel.addElement("Important Notes.txt");
            listModel.addElement("Inspirational Quote.jpg");
            listModel.addElement("Favorite Song.mp3");
        }
        
        // Hide file content view if visible
        showFileContentView(false);
    }
    
    private void showFileContent(String filename) {
        currentView = ViewType.FILE_VIEW;
        backButton.setVisible(true);
        locationLabel.setText(currentFolder + " > " + filename);
        
        // Show the content area
        showFileContentView(true);
        
        if (secretMessages.containsKey(filename)) {
            fileContentArea.setText(secretMessages.get(filename));
        } else {
            fileContentArea.setText("This is a preview of " + filename + "\n\n" +
                                   "(File contents would appear here in a real file system)");
        }
    }
    
    private void showFileContentView(boolean show) {
        if (show) {
            // Remove list and add text area if not already there
            if (contentPanel.getComponentCount() > 1) {
                contentPanel.remove(1); // Remove whatever is in CENTER
            }
            JScrollPane contentScrollPane = new JScrollPane(fileContentArea);
            contentPanel.add(contentScrollPane, BorderLayout.CENTER);
        } else {
            // Remove text area and add list if not already there
            if (contentPanel.getComponentCount() > 1) {
                contentPanel.remove(1); // Remove whatever is in CENTER
            }
            JScrollPane listScrollPane = new JScrollPane(fileList);
            contentPanel.add(listScrollPane, BorderLayout.CENTER);
        }
        
        // Refresh the UI
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void handleListItemClick() {
        if (fileList.getSelectedIndex() == -1) return;
        
        String selectedItem = fileList.getSelectedValue();
        
        if (currentView == ViewType.MAIN_MENU) {
            // If in main menu, navigate to the selected folder
            loadFolderView(selectedItem);
        } else if (currentView == ViewType.FOLDER_VIEW) {
            // If in folder view, open the selected file
            showFileContent(selectedItem);
        }
    }
    
    private void navigateBack() {
        if (currentView == ViewType.FILE_VIEW) {
            loadFolderView(currentFolder);
        } else if (currentView == ViewType.FOLDER_VIEW) {
            loadMainMenu();
        }
    }
    
    // Custom cell renderer to show different icons based on file type
    private class FileListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            
            String text = value.toString();
            
            // Already has an icon in the main menu
            if (currentView == ViewType.MAIN_MENU) {
                return label;
            }
            
            // Add icons based on file extension
            if (text.endsWith(".txt")) {
                label.setText("✉ " + text);
            } else if (text.endsWith(".mp3")) {
                label.setText("♫ " + text);
            } else if (text.endsWith(".jpg") || text.endsWith(".png")) {
                label.setText("☆ " + text);
            } else if (text.endsWith(".mp4")) {
                label.setText("♥ " + text);
            }
            
            return label;
        }
    }
}