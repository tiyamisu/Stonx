package com.stonx.ui;

import com.stonx.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * View Panel for User Registration.
 */
public class RegisterPanel extends JPanel {
    private final MainFrame mainFrame;
    private final UserController userController;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JButton btnGoToLogin;
    private JLabel lblError;

    public RegisterPanel(MainFrame mainFrame, UserController userController) {
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
        cardGbc.insets = new Insets(8, 10, 8, 10);
        cardGbc.gridx = 0;

        // Logo / Title
        JLabel lblTitle = new JLabel("Join StonX", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(new Color(52, 152, 219)); // Vibrant Blue
        cardGbc.gridy = 0;
        cardPanel.add(lblTitle, cardGbc);

        JLabel lblSubtitle = new JLabel("Start your risk-free investing journey", JLabel.CENTER);
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

        txtUsername = new JTextField(20);
        txtUsername.putClientProperty("JTextField.placeholderText", "Create a username");
        cardGbc.gridy = 4;
        cardPanel.add(txtUsername, cardGbc);

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(200, 200, 200));
        cardGbc.gridy = 5;
        cardPanel.add(lblPass, cardGbc);

        txtPassword = new JPasswordField(20);
        txtPassword.putClientProperty("JTextField.placeholderText", "Create a password");
        cardGbc.gridy = 6;
        cardPanel.add(txtPassword, cardGbc);

        // Confirm Password
        JLabel lblConfirmPass = new JLabel("Confirm Password");
        lblConfirmPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblConfirmPass.setForeground(new Color(200, 200, 200));
        cardGbc.gridy = 7;
        cardPanel.add(lblConfirmPass, cardGbc);

        txtConfirmPassword = new JPasswordField(20);
        txtConfirmPassword.putClientProperty("JTextField.placeholderText", "Re-enter your password");
        cardGbc.gridy = 8;
        cardPanel.add(txtConfirmPassword, cardGbc);

        // Register Button
        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setBackground(new Color(52, 152, 219));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.putClientProperty("JButton.buttonType", "roundRect");
        cardGbc.gridy = 9;
        cardGbc.insets = new Insets(20, 10, 10, 10);
        cardPanel.add(btnRegister, cardGbc);

        btnRegister.addActionListener(e -> handleRegister());

        // Switch back to Login panel
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        switchPanel.setBackground(new Color(30, 30, 30));

        JLabel lblHaveAccount = new JLabel("Already have an account?");
        lblHaveAccount.setForeground(new Color(150, 150, 150));
        switchPanel.add(lblHaveAccount);

        btnGoToLogin = new JButton("Login Here");
        btnGoToLogin.setBorderPainted(false);
        btnGoToLogin.setContentAreaFilled(false);
        btnGoToLogin.setForeground(new Color(46, 204, 113)); // Green link
        btnGoToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoToLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGoToLogin.addActionListener(e -> {
            clearFields();
            mainFrame.showPanel("Login");
        });
        switchPanel.add(btnGoToLogin);

        cardGbc.gridy = 10;
        cardGbc.insets = new Insets(10, 10, 10, 10);
        cardPanel.add(switchPanel, cardGbc);

        // Add Card to Main Layout
        add(cardPanel, gbc);
    }

    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            lblError.setText("Please fill out all fields.");
            return;
        }

        if (username.contains(",")) {
            lblError.setText("Username cannot contain commas.");
            return;
        }

        if (!password.equals(confirm)) {
            lblError.setText("Passwords do not match.");
            return;
        }

        boolean success = userController.register(username, password);
        if (success) {
            clearFields();
            mainFrame.onLoginSuccess();
        } else {
            lblError.setText("Username is already taken.");
        }
    }

    public void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        lblError.setText("");
    }
}
