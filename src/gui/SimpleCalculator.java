package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleCalculator extends JInternalFrame implements ActionListener {
    private JTextField display;
    private String operator = "";
    private double firstNumber = 0;
    private boolean newNumber = true;
    
    public SimpleCalculator() {
        super("🔢 Pink Calculator", true, true, true, true);
        initializeCalculator();
    }
    
    private void initializeCalculator() {
        setSize(300, 400);
        setLocation(150, 150);
        
        display = new JTextField("0");
        display.setFont(new Font("Arial", Font.BOLD, 20));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(new Color(255, 250, 250));
        display.setForeground(new Color(139, 69, 19));
        
        JPanel buttonPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        buttonPanel.setBackground(new Color(255, 240, 245));
        
        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", "C", "=", "+"
        };
        
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setBackground(new Color(255, 182, 193));
            button.setForeground(Color.WHITE);
            button.addActionListener(this);
            buttonPanel.add(button);
        }
        
        setLayout(new BorderLayout());
        add(display, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        
        getContentPane().setBackground(new Color(255, 240, 245));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        if ("0123456789".contains(command)) {
            if (newNumber) {
                display.setText(command);
                newNumber = false;
            } else {
                display.setText(display.getText() + command);
            }
        } else if ("+-*/".contains(command)) {
            firstNumber = Double.parseDouble(display.getText());
            operator = command;
            newNumber = true;
        } else if ("=".equals(command)) {
            double secondNumber = Double.parseDouble(display.getText());
            double result = calculate(firstNumber, secondNumber, operator);
            display.setText(String.valueOf(result));
            newNumber = true;
        } else if ("C".equals(command)) {
            display.setText("0");
            firstNumber = 0;
            operator = "";
            newNumber = true;
        }
    }
    
    private double calculate(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return b != 0 ? a / b : 0;
            default: return b;
        }
    }
}
