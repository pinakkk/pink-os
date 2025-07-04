// Version 1.0

// package gui;

// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ActionEvent;

// public class SimpleNotepad extends JInternalFrame {
//     private JTextArea textArea;
    
//     public SimpleNotepad() {
//         super("📝 Pink Notepad", true, true, true, true);
//         initializeNotepad();
//     }
    
//     private void initializeNotepad() {
//         setSize(400, 300);
//         setLocation(100, 100);
        
//         textArea = new JTextArea();
//         textArea.setFont(new Font("Arial", Font.PLAIN, 14));
//         textArea.setBackground(new Color(255, 250, 250));
//         textArea.setForeground(new Color(139, 69, 19));
//         textArea.setText("Welcome to Pink Notepad! 🌸\nWrite your thoughts here...");
        
//         JScrollPane scrollPane = new JScrollPane(textArea);
        
//         JMenuBar menuBar = new JMenuBar();
//         menuBar.setBackground(new Color(255, 182, 193));
        
//         JMenu fileMenu = new JMenu("File");
//         fileMenu.add(new JMenuItem(new AbstractAction("New") {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 textArea.setText("");
//             }
//         }));
//         fileMenu.add(new JMenuItem(new AbstractAction("Clear") {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 textArea.setText("");
//             }
//         }));
        
//         menuBar.add(fileMenu);
//         setJMenuBar(menuBar);
        
//         add(scrollPane);
        
//         getContentPane().setBackground(new Color(255, 240, 245));
//     }
// }


// Version 2.0

package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleNotepad extends JInternalFrame {
    private JTextArea textArea;
    private JLabel statusLabel;
    private JLabel charCountLabel;
    private JLabel timeLabel;
    private Timer timer;
    private Font textFont;
    
    public SimpleNotepad() {
        super("✎ Pink Notepad", true, true, true, true);
        initializeNotepad();
    }
    
    private void initializeNotepad() {
        setSize(500, 400);
        setLocation(100, 100);
        
        // Try to use a nicer font
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = ge.getAvailableFontFamilyNames();
            
            // Try to find a nice font
            if (containsIgnoreCase(fontNames, "Segoe UI")) {
                textFont = new Font("Segoe UI", Font.PLAIN, 14);
            } else if (containsIgnoreCase(fontNames, "Montserrat")) {
                textFont = new Font("Montserrat", Font.PLAIN, 14);
            } else {
                textFont = new Font("SansSerif", Font.PLAIN, 14);
            }
        } catch (Exception e) {
            textFont = new Font("SansSerif", Font.PLAIN, 14);
        }
        
        // Set up the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 240, 245));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Create text area with styling
        textArea = new JTextArea();
        textArea.setFont(textFont);
        textArea.setBackground(new Color(255, 250, 250));
        textArea.setForeground(new Color(139, 69, 19));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(8, 8, 8, 8));
        textArea.setText("Welcome to Pink Notepad! ♥\nWrite your thoughts here...");
        
        // Add document listener to track changes
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateStatus(); }
            
            @Override
            public void removeUpdate(DocumentEvent e) { updateStatus(); }
            
            @Override
            public void changedUpdate(DocumentEvent e) { updateStatus(); }
        });
        
        // Add caret listener to track cursor position
        textArea.addCaretListener(e -> {
            try {
                int pos = e.getDot();
                int line = textArea.getLineOfOffset(pos);
                int col = pos - textArea.getLineStartOffset(line);
                statusLabel.setText("Line: " + (line + 1) + ", Col: " + (col + 1));
            } catch (Exception ex) {
                statusLabel.setText("Line: 1, Col: 1");
            }
        });
        
        // Create line number component
        TextLineNumber lineNumber = new TextLineNumber(textArea);
        
        // Add scrollpane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumber);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 182, 193), 1));
        
        // Create toolbar
        JPanel toolBar = new JPanel();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolBar.setBackground(new Color(255, 228, 225));
        toolBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        // Font selector
        String[] fontSizes = {"12", "14", "16", "18", "20", "22", "24"};
        JComboBox<String> fontSizeBox = new JComboBox<>(fontSizes);
        fontSizeBox.setSelectedItem("14");
        fontSizeBox.addActionListener(e -> {
            try {
                int fontSize = Integer.parseInt((String)fontSizeBox.getSelectedItem());
                textArea.setFont(new Font(textFont.getFamily(), Font.PLAIN, fontSize));
            } catch (Exception ex) {
                // Ignore parsing errors
            }
        });
        
        JButton boldButton = createToolbarButton("B");
        boldButton.setFont(new Font(boldButton.getFont().getFamily(), Font.BOLD, 12));
        boldButton.addActionListener(e -> toggleBold());
        
        JButton clearButton = createToolbarButton("Clear");
        clearButton.addActionListener(e -> textArea.setText(""));
        
        toolBar.add(new JLabel("Size: "));
        toolBar.add(fontSizeBox);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(boldButton);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(clearButton);
        
        // Create status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(255, 228, 225));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        statusLabel = new JLabel("Line: 1, Col: 1");
        charCountLabel = new JLabel("Characters: 0");
        timeLabel = new JLabel();
        
        // Update time every second
        updateTime();
        timer = new Timer(1000, e -> updateTime());
        timer.start();
        
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftStatus.setBackground(new Color(255, 228, 225));
        leftStatus.add(statusLabel);
        leftStatus.add(Box.createHorizontalStrut(15));
        leftStatus.add(charCountLabel);
        
        statusBar.add(leftStatus, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        // Add components to main panel
        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(255, 182, 193));
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        
        // File menu
        JMenu fileMenu = createMenu("File");
        fileMenu.add(createMenuItem("New", 'N', e -> textArea.setText("")));
        fileMenu.add(createMenuItem("Save As...", 'S', e -> showSaveMessage()));
        fileMenu.add(createMenuItem("Open...", 'O', e -> showOpenMessage()));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", 'Q', e -> dispose()));
        
        // Edit menu
        JMenu editMenu = createMenu("Edit");
        editMenu.add(createMenuItem("Cut", 'X', e -> textArea.cut()));
        editMenu.add(createMenuItem("Copy", 'C', e -> textArea.copy()));
        editMenu.add(createMenuItem("Paste", 'V', e -> textArea.paste()));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Select All", 'A', e -> textArea.selectAll()));
        
        // Format menu
        JMenu formatMenu = createMenu("Format");
        formatMenu.add(createMenuItem("Word Wrap", 'W', e -> {
            boolean wrap = !textArea.getLineWrap();
            textArea.setLineWrap(wrap);
            textArea.setWrapStyleWord(wrap);
        }));
        
        // Help menu
        JMenu helpMenu = createMenu("Help");
        helpMenu.add(createMenuItem("About", e -> showAboutDialog()));
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
        setContentPane(mainPanel);
        
        // Update status initially
        updateStatus();
    }
    
    private boolean containsIgnoreCase(String[] array, String value) {
        for (String item : array) {
            if (item.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
    
    private JButton createToolbarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(textFont.deriveFont(12f));
        button.setBackground(new Color(255, 192, 203));
        button.setForeground(new Color(139, 69, 19));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        return button;
    }
    
    private JMenu createMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(textFont);
        menu.setForeground(new Color(139, 69, 19));
        return menu;
    }
    
    private JMenuItem createMenuItem(String text, ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(textFont);
        item.setBackground(new Color(255, 240, 245));
        item.setForeground(new Color(139, 69, 19));
        item.addActionListener(listener);
        return item;
    }
    
    private JMenuItem createMenuItem(String text, char mnemonic, ActionListener listener) {
        JMenuItem item = createMenuItem(text, listener);
        item.setMnemonic(mnemonic);
        item.setAccelerator(KeyStroke.getKeyStroke(mnemonic, InputEvent.CTRL_DOWN_MASK));
        return item;
    }
    
    private void updateStatus() {
        int chars = textArea.getText().length();
        charCountLabel.setText("Characters: " + chars);
    }
    
    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        timeLabel.setText(sdf.format(new Date()));
    }
    
    private void toggleBold() {
        Font currentFont = textArea.getFont();
        int style = (currentFont.getStyle() == Font.BOLD) ? Font.PLAIN : Font.BOLD;
        textArea.setFont(new Font(currentFont.getFamily(), style, currentFont.getSize()));
    }
    
    private void showSaveMessage() {
        JOptionPane.showMessageDialog(this, 
            "This would save your file in a real application!",
            "Save File", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showOpenMessage() {
        JOptionPane.showMessageDialog(this, 
            "This would open a file in a real application!",
            "Open File", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Pink Notepad\nVersion 1.0\nCreated with ♥ for PinkOS",
            "About Pink Notepad",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Line number component
    class TextLineNumber extends JPanel {
        private final JTextComponent textComponent;
        private final FontMetrics fontMetrics;
        
        public TextLineNumber(JTextComponent component) {
            textComponent = component;
            setFont(component.getFont());
            fontMetrics = getFontMetrics(getFont());
            setPreferredWidth();
            setBackground(new Color(255, 245, 250));
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        }
        
        private void setPreferredWidth() {
            int digits = String.valueOf(textComponent.getDocument().getDefaultRootElement().getElementCount()).length();
            int width = Math.max(digits, 2) * fontMetrics.charWidth('0') + 12;
            setPreferredSize(new Dimension(width, 0));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Set rendering hints for better text quality
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            Rectangle clip = g.getClipBounds();
            int lineHeight = fontMetrics.getHeight();
            int startLine = clip.y / lineHeight;
            int endLine = (clip.y + clip.height) / lineHeight + 1;
            
            Element root = textComponent.getDocument().getDefaultRootElement();
            int totalLines = root.getElementCount();
            
            for (int i = startLine; i < endLine && i < totalLines; i++) {
                String lineNumber = String.valueOf(i + 1);
                int width = fontMetrics.stringWidth(lineNumber);
                int x = getWidth() - width - 5;
                int y = i * lineHeight + fontMetrics.getAscent();
                
                g.setColor(new Color(205, 92, 92));
                g.drawString(lineNumber, x, y);
            }
            
            setPreferredWidth();
        }
    }
}