package com.stonx.ui;

import com.stonx.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * View Panel for User Login.
 * Designed with a clean, centered modern card.
 */
public class LoginPanel extends JPanel {
    private final MainFrame mainFrame;
    private final UserController userController;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnGoToRegister;
    private JLabel lblError;

    public LoginPanel(MainFrame mainFrame, UserController userController) {
        this.mainFrame = mainFrame;
        this.userController = userController;

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Center card panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 45), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));
        cardPanel.setBackground(new Color(30, 30, 30));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.insets = new Insets(10, 10, 10, 10);
        cardGbc.gridx = 0;

        // Logo / Title
        JLabel lblTitle = new JLabel("StonX", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(new Color(46, 204, 113)); // Vibrant Green
        cardGbc.gridy = 0;
        cardPanel.add(lblTitle, cardGbc);

        JLabel lblSubtitle = new JLabel("Virtual Stock Market Simulator", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(150, 150, 150));
        cardGbc.gridy = 1;
        cardPanel.add(lblSubtitle, cardGbc);

        // Error message
        lblError = new JLabel("", JLabel.CENTER);
        lblError.setForeground(new Color(231, 76, 60)); // Red
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cardGbc.gridy = 2;
        cardPanel.add(lblError, cardGbc);

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(200, 200, 200));
        cardGbc.gridy = 3;
        cardPanel.add(lblUser, cardGbc);

        txtUsername = new JTextField("java_demo", 20);
        txtUsername.putClientProperty("JTextField.placeholderText", "Enter your username");
        cardGbc.gridy = 4;
        cardPanel.add(txtUsername, cardGbc);

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(200, 200, 200));
        cardGbc.gridy = 5;
        cardPanel.add(lblPass, cardGbc);

        txtPassword = new JPasswordField("1234", 20);
        txtPassword.putClientProperty("JTextField.placeholderText", "Enter your password");
        cardGbc.gridy = 6;
        cardPanel.add(txtPassword, cardGbc);

        // Login Button
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");
        cardGbc.gridy = 7;
        cardGbc.insets = new Insets(20, 10, 10, 10);
        cardPanel.add(btnLogin, cardGbc);

        // Action Listener
        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addActionListener(e -> handleLogin()); // Press enter to login

        // Switch to register label & button
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        switchPanel.setBackground(new Color(30, 30, 30));
        
        JLabel lblNoAccount = new JLabel("New to StonX?");
        lblNoAccount.setForeground(new Color(150, 150, 150));
        switchPanel.add(lblNoAccount);

        btnGoToRegister = new JButton("Create Account");
        btnGoToRegister.setBorderPainted(false);
        btnGoToRegister.setContentAreaFilled(false);
        btnGoToRegister.setForeground(new Color(52, 152, 219)); // Blue link
        btnGoToRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoToRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGoToRegister.addActionListener(e -> {
            clearFields();
            mainFrame.showPanel("Register");
        });
        switchPanel.add(btnGoToRegister);

        cardGbc.gridy = 8;
        cardGbc.insets = new Insets(10, 10, 10, 10);
        cardPanel.add(switchPanel, cardGbc);

        // Add Card to Main Layout
        add(cardPanel, gbc);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            return;
        }

        boolean success = userController.login(username, password);
        if (success) {
            clearFields();
            mainFrame.onLoginSuccess();
        } else {
            lblError.setText("Invalid username or password.");
        }
    }

    public void clearFields() {
        txtUsername.setText("java_demo");
        txtPassword.setText("1234");
        lblError.setText("");
    }
}
