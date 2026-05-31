package com.stonx.ui;

import com.stonx.controller.UserController;
import com.stonx.controller.StockController;
import com.stonx.model.Stock;
import com.stonx.ui.components.ModernCardPanel;
import com.stonx.utils.StockObserver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Beautiful, presentation-worthy Landing Screen.
 * Contains scrolling ticker, market index indicators, trending gainers, and auth forms.
 */
public class LandingPanel extends JPanel implements StockObserver {
    private final MainFrame mainFrame;
    private final UserController userController;
    private final StockController stockController;

    // Scrolling Ticker
    private TickerRibbon tickerRibbon;

    // Index Labels
    private JLabel lblNiftyPrice;
    private JLabel lblNiftyChange;
    private JLabel lblSensexPrice;
    private JLabel lblSensexChange;

    // Trending Panel rows
    private JPanel panelTrendingRows;

    // Local Auth Card layout
    private CardLayout authCardLayout;
    private JPanel panelAuthCards;

    // Login Fields
    private JTextField txtLoginUser;
    private JPasswordField txtLoginPass;
    private JLabel lblLoginError;

    // Register Fields
    private JTextField txtRegUser;
    private JPasswordField txtRegPass;
    private JPasswordField txtRegConfirm;
    private JLabel lblRegError;

    private final DecimalFormat priceFormat = new DecimalFormat("₹#,##0.00");
    private final DecimalFormat percentFormat = new DecimalFormat("+#,##0.00%;-#,##0.00%");

    public LandingPanel(MainFrame mainFrame, UserController userCtrl, StockController stockCtrl) {
        this.mainFrame = mainFrame;
        this.userController = userCtrl;
        this.stockController = stockCtrl;

        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));

        initComponents();

        // Register as stock market observer to update indexes & trending list
        stockController.registerObserver(this);
    }

    private void initComponents() {
        // 1. TOP TICKER RIBBON
        tickerRibbon = new TickerRibbon();
        add(tickerRibbon, BorderLayout.NORTH);

        // 2. MAIN BODY (Split Layout)
        JPanel mainBody = new JPanel(new GridBagLayout());
        mainBody.setBackground(new Color(20, 20, 20));
        mainBody.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Showcase Column (Weight = 0.6)
        JPanel leftShowcase = new JPanel(new GridBagLayout());
        leftShowcase.setBackground(new Color(20, 20, 20));
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.anchor = GridBagConstraints.NORTHWEST;
        leftGbc.weightx = 1.0;
        leftGbc.gridx = 0;

        // Brand Logo & Tagline
        JLabel lblLogo = new JLabel("StonX");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 54));
        lblLogo.setForeground(new Color(46, 204, 113)); // Vibrant Emerald Green
        leftGbc.gridy = 0;
        leftGbc.insets = new Insets(0, 0, 5, 0);
        leftShowcase.add(lblLogo, leftGbc);

        JLabel lblTagline = new JLabel("Learn. Invest. Trade. Grow.");
        lblTagline.setFont(new Font("Segoe UI Light", Font.PLAIN, 24));
        lblTagline.setForeground(new Color(200, 200, 200));
        leftGbc.gridy = 1;
        leftGbc.insets = new Insets(0, 0, 25, 0);
        leftShowcase.add(lblTagline, leftGbc);

        // Subtitle Description
        JLabel lblDesc = new JLabel("<html>Experience the rush of the Indian stock market in real-time.<br>" +
                "Build strategies, track portfolio net worth, and leverage virtual intelligence.</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(new Color(150, 150, 150));
        leftGbc.gridy = 2;
        leftGbc.insets = new Insets(0, 0, 30, 0);
        leftShowcase.add(lblDesc, leftGbc);

        // Indices stats row (Nifty 50 and Sensex cards)
        JPanel rowIndices = new JPanel(new GridLayout(1, 2, 20, 0));
        rowIndices.setBackground(new Color(20, 20, 20));

        // Nifty 50 Card
        ModernCardPanel niftyCard = new ModernCardPanel(12);
        niftyCard.setAccentStripColor(new Color(52, 152, 219));
        niftyCard.setLayout(new BorderLayout());
        niftyCard.setBorder(new EmptyBorder(10, 20, 10, 15));
        
        JLabel lblNiftyTitle = new JLabel("NIFTY 50", JLabel.LEFT);
        lblNiftyTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNiftyTitle.setForeground(new Color(150, 150, 150));
        niftyCard.add(lblNiftyTitle, BorderLayout.NORTH);

        JPanel panelNiftyVals = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelNiftyVals.setOpaque(false);
        lblNiftyPrice = new JLabel("₹18,500.00");
        lblNiftyPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNiftyPrice.setForeground(Color.WHITE);
        lblNiftyChange = new JLabel("+0.00%");
        lblNiftyChange.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNiftyChange.setForeground(new Color(46, 204, 113));
        panelNiftyVals.add(lblNiftyPrice);
        panelNiftyVals.add(lblNiftyChange);
        niftyCard.add(panelNiftyVals, BorderLayout.CENTER);

        // Sensex Card
        ModernCardPanel sensexCard = new ModernCardPanel(12);
        sensexCard.setAccentStripColor(new Color(155, 89, 182));
        sensexCard.setLayout(new BorderLayout());
        sensexCard.setBorder(new EmptyBorder(10, 20, 10, 15));

        JLabel lblSensexTitle = new JLabel("SENSEX", JLabel.LEFT);
        lblSensexTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSensexTitle.setForeground(new Color(150, 150, 150));
        sensexCard.add(lblSensexTitle, BorderLayout.NORTH);

        JPanel panelSensexVals = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelSensexVals.setOpaque(false);
        lblSensexPrice = new JLabel("₹61,000.00");
        lblSensexPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSensexPrice.setForeground(Color.WHITE);
        lblSensexChange = new JLabel("+0.00%");
        lblSensexChange.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSensexChange.setForeground(new Color(46, 204, 113));
        panelSensexVals.add(lblSensexPrice);
        panelSensexVals.add(lblSensexChange);
        sensexCard.add(panelSensexVals, BorderLayout.CENTER);

        rowIndices.add(niftyCard);
        rowIndices.add(sensexCard);

        leftGbc.gridy = 3;
        leftGbc.insets = new Insets(0, 0, 30, 0);
        leftShowcase.add(rowIndices, leftGbc);

        // Trending Stock Tickers
        ModernCardPanel trendingPanel = new ModernCardPanel(14);
        trendingPanel.setLayout(new BorderLayout());
        trendingPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTrendingTitle = new JLabel("🔥 Top Gainers (Live Simulation)");
        lblTrendingTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTrendingTitle.setForeground(Color.WHITE);
        lblTrendingTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        trendingPanel.add(lblTrendingTitle, BorderLayout.NORTH);

        panelTrendingRows = new JPanel(new GridLayout(3, 1, 0, 8));
        panelTrendingRows.setOpaque(false);
        trendingPanel.add(panelTrendingRows, BorderLayout.CENTER);

        leftGbc.gridy = 4;
        leftGbc.weighty = 1.0;
        leftGbc.insets = new Insets(0, 0, 0, 0);
        leftShowcase.add(trendingPanel, leftGbc);

        // Right Auth Panel (Weight = 0.4)
        JPanel rightCol = new JPanel(new GridBagLayout());
        rightCol.setBackground(new Color(20, 20, 20));
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.anchor = GridBagConstraints.CENTER;

        // Card Container Panel (Deep glassmorphism card style)
        ModernCardPanel authContainer = new ModernCardPanel(16);
        authContainer.setHoverEnabled(false);
        authContainer.setPreferredSize(new Dimension(380, 420));
        authContainer.setLayout(new BorderLayout());

        authCardLayout = new CardLayout();
        panelAuthCards = new JPanel(authCardLayout);
        panelAuthCards.setOpaque(false);

        // Build Login Card & Register Card
        buildLoginCard();
        buildRegisterCard();

        panelAuthCards.add(createStyledCardWrapper(loginPanelCard()), "Login");
        panelAuthCards.add(createStyledCardWrapper(registerPanelCard()), "Register");
        authContainer.add(panelAuthCards, BorderLayout.CENTER);

        rightCol.add(authContainer, rightGbc);

        // Assemble Left & Right in GridBagLayout
        gbc.gridx = 0;
        gbc.weightx = 0.6;
        mainBody.add(leftShowcase, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        mainBody.add(rightCol, gbc);

        add(mainBody, BorderLayout.CENTER);
    }

    private JPanel createStyledCardWrapper(JPanel inner) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(25, 30, 25, 30));
        wrapper.add(inner, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel loginPanelCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Header Title
        JLabel lblTitle = new JLabel("Welcome Back", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        panel.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Log in to check portfolio & trade", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(150, 150, 150));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(lblSub, gbc);

        // Error log
        lblLoginError = new JLabel("", JLabel.CENTER);
        lblLoginError.setForeground(new Color(231, 76, 60));
        lblLoginError.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(lblLoginError, gbc);

        // Username field
        gbc.insets = new Insets(6, 0, 2, 0);
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUser.setForeground(new Color(180, 180, 180));
        gbc.gridy = 3;
        panel.add(lblUser, gbc);

        txtLoginUser = new JTextField("java_demo");
        txtLoginUser.putClientProperty("JTextField.placeholderText", "Enter username");
        txtLoginUser.setPreferredSize(new Dimension(0, 36));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(txtLoginUser, gbc);

        // Password field
        gbc.insets = new Insets(6, 0, 2, 0);
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPass.setForeground(new Color(180, 180, 180));
        gbc.gridy = 5;
        panel.add(lblPass, gbc);

        txtLoginPass = new JPasswordField("1234");
        txtLoginPass.putClientProperty("JTextField.placeholderText", "Enter password");
        txtLoginPass.setPreferredSize(new Dimension(0, 36));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(txtLoginPass, gbc);

        // Login Button
        JButton btnLogin = new JButton("Access Platform");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");
        btnLogin.setPreferredSize(new Dimension(0, 38));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> handleLogin());
        txtLoginPass.addActionListener(e -> handleLogin());
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 0, 15, 0);
        panel.add(btnLogin, gbc);

        // Toggle to register
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        switchPanel.setOpaque(false);
        JLabel lblNoAcc = new JLabel("New to StonX?");
        lblNoAcc.setForeground(new Color(150, 150, 150));
        switchPanel.add(lblNoAcc);

        JButton btnRegisterLink = new JButton("Create Account");
        btnRegisterLink.setBorderPainted(false);
        btnRegisterLink.setContentAreaFilled(false);
        btnRegisterLink.setForeground(new Color(52, 152, 219));
        btnRegisterLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegisterLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegisterLink.addActionListener(e -> {
            lblLoginError.setText("");
            authCardLayout.show(panelAuthCards, "Register");
        });
        switchPanel.add(btnRegisterLink);

        gbc.gridy = 8;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(switchPanel, gbc);

        return panel;
    }

    private JPanel registerPanelCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(3, 0, 3, 0);

        // Header Title
        JLabel lblTitle = new JLabel("Join StonX", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        panel.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Register your trading simulation profile", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(150, 150, 150));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(lblSub, gbc);

        // Error log
        lblRegError = new JLabel("", JLabel.CENTER);
        lblRegError.setForeground(new Color(231, 76, 60));
        lblRegError.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(lblRegError, gbc);

        // Username field
        gbc.insets = new Insets(4, 0, 1, 0);
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUser.setForeground(new Color(180, 180, 180));
        gbc.gridy = 3;
        panel.add(lblUser, gbc);

        txtRegUser = new JTextField();
        txtRegUser.putClientProperty("JTextField.placeholderText", "Create username");
        txtRegUser.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(txtRegUser, gbc);

        // Password field
        gbc.insets = new Insets(4, 0, 1, 0);
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPass.setForeground(new Color(180, 180, 180));
        gbc.gridy = 5;
        panel.add(lblPass, gbc);

        txtRegPass = new JPasswordField();
        txtRegPass.putClientProperty("JTextField.placeholderText", "Create password");
        txtRegPass.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(txtRegPass, gbc);

        // Confirm Password field
        gbc.insets = new Insets(4, 0, 1, 0);
        JLabel lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblConfirm.setForeground(new Color(180, 180, 180));
        gbc.gridy = 7;
        panel.add(lblConfirm, gbc);

        txtRegConfirm = new JPasswordField();
        txtRegConfirm.putClientProperty("JTextField.placeholderText", "Re-enter password");
        txtRegConfirm.setPreferredSize(new Dimension(0, 32));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(txtRegConfirm, gbc);

        // Register Button
        JButton btnRegister = new JButton("Create Free Account");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setBackground(new Color(52, 152, 219));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.putClientProperty("JButton.buttonType", "roundRect");
        btnRegister.setPreferredSize(new Dimension(0, 36));
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> handleRegister());
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 0, 10, 0);
        panel.add(btnRegister, gbc);

        // Toggle back to login
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        switchPanel.setOpaque(false);
        JLabel lblHasAcc = new JLabel("Have an account?");
        lblHasAcc.setForeground(new Color(150, 150, 150));
        switchPanel.add(lblHasAcc);

        JButton btnLoginLink = new JButton("Log In");
        btnLoginLink.setBorderPainted(false);
        btnLoginLink.setContentAreaFilled(false);
        btnLoginLink.setForeground(new Color(46, 204, 113));
        btnLoginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLoginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLoginLink.addActionListener(e -> {
            lblRegError.setText("");
            authCardLayout.show(panelAuthCards, "Login");
        });
        switchPanel.add(btnLoginLink);

        gbc.gridy = 10;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(switchPanel, gbc);

        return panel;
    }

    private void buildLoginCard() {
        // Card layout helper triggers loginPanelCard logic
    }

    private void buildRegisterCard() {
        // Card layout helper triggers registerPanelCard logic
    }

    private void handleLogin() {
        String user = txtLoginUser.getText().trim();
        String pass = new String(txtLoginPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblLoginError.setText("Please fill out all fields.");
            return;
        }

        boolean success = userController.login(user, pass);
        if (success) {
            clearFields();
            mainFrame.onLoginSuccess();
        } else {
            lblLoginError.setText("Invalid credentials.");
        }
    }

    private void handleRegister() {
        String user = txtRegUser.getText().trim();
        String pass = new String(txtRegPass.getPassword());
        String confirm = new String(txtRegConfirm.getPassword());

        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            lblRegError.setText("Please fill out all fields.");
            return;
        }

        if (user.contains(",")) {
            lblRegError.setText("Username cannot contain commas.");
            return;
        }

        if (!pass.equals(confirm)) {
            lblRegError.setText("Passwords do not match.");
            return;
        }

        boolean success = userController.register(user, pass);
        if (success) {
            clearFields();
            mainFrame.onLoginSuccess();
        } else {
            lblRegError.setText("Username is already taken.");
        }
    }

    private void clearFields() {
        txtLoginUser.setText("java_demo");
        txtLoginPass.setText("1234");
        txtRegUser.setText("");
        txtRegPass.setText("");
        txtRegConfirm.setText("");
        lblLoginError.setText("");
        lblRegError.setText("");
    }

    public void cleanup() {
        if (tickerRibbon != null) {
            tickerRibbon.stop();
        }
        stockController.removeObserver(this);
    }

    @Override
    public void onStockUpdate(List<Stock> stocks) {
        SwingUtilities.invokeLater(() -> {
            // Update Nifty & Sensex Index estimations
            double avgChange = 0.0;
            for (Stock s : stocks) {
                avgChange += s.getDailyChangePercent();
            }
            avgChange = avgChange / stocks.size();

            // Mock base values
            double niftyPrice = 18500.0 * (1.0 + (avgChange / 100.0));
            double sensexPrice = 61000.0 * (1.0 + (avgChange / 100.0));

            lblNiftyPrice.setText(priceFormat.format(niftyPrice));
            lblNiftyChange.setText(String.format("%s%.2f%%", (avgChange >= 0 ? "+" : ""), avgChange));
            if (avgChange >= 0) {
                lblNiftyChange.setForeground(new Color(46, 204, 113));
            } else {
                lblNiftyChange.setForeground(new Color(231, 76, 60));
            }

            lblSensexPrice.setText(priceFormat.format(sensexPrice));
            lblSensexChange.setText(String.format("%s%.2f%%", (avgChange >= 0 ? "+" : ""), avgChange));
            if (avgChange >= 0) {
                lblSensexChange.setForeground(new Color(46, 204, 113));
            } else {
                lblSensexChange.setForeground(new Color(231, 76, 60));
            }

            // Populate Top 3 Gainers rows
            List<Stock> sorted = new ArrayList<>(stocks);
            sorted.sort(Comparator.comparingDouble(Stock::getDailyChangePercent).reversed());

            panelTrendingRows.removeAll();
            for (int i = 0; i < Math.min(3, sorted.size()); i++) {
                Stock s = sorted.get(i);
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);

                JLabel lblName = new JLabel(s.getSymbol() + "  " + s.getName());
                lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblName.setForeground(new Color(220, 220, 220));

                JLabel lblPrice = new JLabel(priceFormat.format(s.getCurrentPrice()) + " (" + 
                        String.format("+%.2f%%", s.getDailyChangePercent()) + ")");
                lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblPrice.setForeground(new Color(46, 204, 113));

                row.add(lblName, BorderLayout.WEST);
                row.add(lblPrice, BorderLayout.EAST);
                panelTrendingRows.add(row);
            }
            panelTrendingRows.revalidate();
            panelTrendingRows.repaint();
        });
    }

    /**
     * Scrolling Ticker Ribbon inner component.
     */
    private class TickerRibbon extends JPanel {
        private int offset = 0;
        private final Timer timer;

        public TickerRibbon() {
            setPreferredSize(new Dimension(0, 36));
            setBackground(new Color(25, 25, 25));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 45, 45)));

            timer = new Timer(25, e -> {
                offset -= 1;
                // loop length
                if (offset < -1400) {
                    offset = 0;
                }
                repaint();
            });
            timer.start();
        }

        public void stop() {
            timer.stop();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));

            List<Stock> allStocks = stockController.getAllStocks();
            if (allStocks.isEmpty()) {
                g2.dispose();
                return;
            }

            int x = offset;
            for (int loop = 0; loop < 3; loop++) {
                for (Stock stock : allStocks) {
                    String sym = stock.getSymbol();
                    double price = stock.getCurrentPrice();
                    double change = stock.getDailyChangePercent();
                    String changeText = String.format("%s%.2f%%", (change >= 0 ? "+" : ""), change);

                    g2.setColor(new Color(200, 200, 200));
                    g2.drawString(sym + "  ", x, 22);
                    x += g2.getFontMetrics().stringWidth(sym + "  ");

                    g2.setColor(Color.WHITE);
                    g2.drawString(priceFormat.format(price) + "  ", x, 22);
                    x += g2.getFontMetrics().stringWidth(priceFormat.format(price) + "  ");

                    if (change >= 0) {
                        g2.setColor(new Color(46, 204, 113));
                    } else {
                        g2.setColor(new Color(231, 76, 60));
                    }
                    g2.drawString(changeText + "   |   ", x, 22);
                    x += g2.getFontMetrics().stringWidth(changeText + "   |   ");
                }
            }
            g2.dispose();
        }
    }
}
