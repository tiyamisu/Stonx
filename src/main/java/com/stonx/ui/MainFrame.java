package com.stonx.ui;

import com.stonx.controller.PortfolioController;
import com.stonx.controller.StockController;
import com.stonx.controller.UserController;
import com.stonx.model.Stock;
import com.stonx.model.User;
import com.stonx.utils.StockObserver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Shell window of the StonX application.
 * Manages view routing via CardLayout, sidebar navigation, top bar (clock & status),
 * and footer live stock marquee ribbon.
 */
public class MainFrame extends JFrame implements StockObserver {
    private final StockController stockController;
    private final UserController userController;
    private final PortfolioController portfolioController;

    // View panels
    private LandingPanel landingPanel;
    private DashboardPanel dashboardPanel;
    private PortfolioPanel portfolioPanel;
    private WatchlistPanel watchlistPanel;
    private TransactionPanel transactionPanel;
    private LeaderboardPanel leaderboardPanel;
    private StonBotPanel stonBotPanel;

    // Visual routing components
    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;

    // Layout containers
    private JPanel sidebarPanel;
    private JPanel topBarPanel;
    private FooterTickerRibbon footerTicker;

    // Top Bar labels
    private JLabel lblPageTitle;
    private JLabel lblClock;

    // Sidebar details
    private JLabel lblUserAccount;
    private JLabel lblUserBalance;
    private JButton[] navButtons;
    private final String[] navTabNames = {"Dashboard", "Portfolio", "Watchlist", "Transactions", "Leaderboard", "StonBot"};

    private final DecimalFormat priceFormat = new DecimalFormat("₹#,##0.00");
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy   HH:mm:ss");

    private Timer clockTimer;

    public MainFrame() {
        // Initialize MVC Controllers
        this.stockController = new StockController();
        this.userController = new UserController();
        this.portfolioController = new PortfolioController();

        setTitle("StonX - Virtual Stock Trading Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);

        // Start stock simulation immediately so ticker scrolls on Landing screen
        stockController.startSimulation();

        // Close hook: ensure user data is saved on frame exit
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                userController.saveUserData();
                stockController.stopSimulation();
                if (landingPanel != null) {
                    landingPanel.cleanup();
                }
                if (footerTicker != null) {
                    footerTicker.stop();
                }
            }
        });

        initViews();
        showLoggedOutLayout();
    }

    private void initViews() {
        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);
        mainCardPanel.setBackground(new Color(20, 20, 20));

        // 1. Landing Panel (Login / Register combined showcase)
        landingPanel = new LandingPanel(this, userController, stockController);
        mainCardPanel.add(landingPanel, "Landing");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainCardPanel, BorderLayout.CENTER);

        // Initialize clock timer for Top Navigation Bar
        clockTimer = new Timer(1000, e -> {
            if (lblClock != null) {
                lblClock.setText(LocalDateTime.now().format(timeFormat));
            }
        });
    }

    private void showLoggedOutLayout() {
        // Hide shell elements if they exist
        if (sidebarPanel != null) getContentPane().remove(sidebarPanel);
        if (topBarPanel != null) getContentPane().remove(topBarPanel);
        if (footerTicker != null) {
            footerTicker.stop();
            getContentPane().remove(footerTicker);
        }

        clockTimer.stop();
        mainCardLayout.show(mainCardPanel, "Landing");
        revalidate();
        repaint();
    }

    /**
     * Set up main trading views, build side bar, start price updates.
     */
    public void onLoginSuccess() {
        User currentUser = userController.getCurrentUser();
        if (currentUser == null) return;

        // Initialize active trading panels
        dashboardPanel = new DashboardPanel(this, stockController, userController, portfolioController);
        portfolioPanel = new PortfolioPanel(this, portfolioController, stockController);
        watchlistPanel = new WatchlistPanel(this, stockController, userController, portfolioController);
        transactionPanel = new TransactionPanel(this, portfolioController);
        leaderboardPanel = new LeaderboardPanel(this, userController);
        stonBotPanel = new StonBotPanel(this);

        mainCardPanel.add(dashboardPanel, "Dashboard");
        mainCardPanel.add(portfolioPanel, "Portfolio");
        mainCardPanel.add(watchlistPanel, "Watchlist");
        mainCardPanel.add(transactionPanel, "Transactions");
        mainCardPanel.add(leaderboardPanel, "Leaderboard");
        mainCardPanel.add(stonBotPanel, "StonBot");

        // 1. Build & Add Sidebar navigation (WEST)
        buildSidebar(currentUser.getUsername());
        getContentPane().add(sidebarPanel, BorderLayout.WEST);

        // 2. Build & Add Top Navigation Header (NORTH)
        buildTopNav();
        getContentPane().add(topBarPanel, BorderLayout.NORTH);

        // 3. Build & Add Footer live scrolling ticker marquee (SOUTH)
        footerTicker = new FooterTickerRibbon();
        getContentPane().add(footerTicker, BorderLayout.SOUTH);

        // Start Clock
        clockTimer.start();

        // Register for stock updates to sync footer ticker ribbon
        stockController.registerObserver(this);

        // Show Dashboard by default
        showTab("Dashboard");

        // Show Opening Bell Welcome Toast
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    "🔔 Welcome back, @" + currentUser.getUsername() + "!\n" +
                    "Indian Markets are Open. Your current balance is " + priceFormat.format(currentUser.getBalance()) + ".",
                    "StonX Market Opening Bell", JOptionPane.INFORMATION_MESSAGE);
        });

        revalidate();
        repaint();
    }

    private void buildTopNav() {
        topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(new Color(25, 25, 25));
        topBarPanel.setPreferredSize(new Dimension(0, 50));
        topBarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 45, 45)));

        // Left Panel: Active Page Title
        lblPageTitle = new JLabel("DASHBOARD", JLabel.LEFT);
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPageTitle.setForeground(Color.WHITE);
        lblPageTitle.setBorder(new EmptyBorder(0, 20, 0, 0));
        topBarPanel.add(lblPageTitle, BorderLayout.WEST);

        // Right Panel: Market status + clock
        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        rightInfo.setOpaque(false);

        JLabel lblMarketBadge = new JLabel("● MARKET OPEN");
        lblMarketBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMarketBadge.setForeground(new Color(46, 204, 113)); // Bright green status dot
        rightInfo.add(lblMarketBadge);

        lblClock = new JLabel(LocalDateTime.now().format(timeFormat));
        lblClock.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        lblClock.setForeground(new Color(180, 180, 180));
        rightInfo.add(lblClock);

        topBarPanel.add(rightInfo, BorderLayout.EAST);
    }

    private void buildSidebar(String username) {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(new Color(30, 30, 30));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(45, 45, 45)));

        // 1. Logo Panel
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(30, 30, 30));
        logoPanel.setBorder(new EmptyBorder(25, 20, 15, 20));

        JLabel lblLogo = new JLabel("StonX", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(new Color(46, 204, 113));
        logoPanel.add(lblLogo, BorderLayout.CENTER);

        sidebarPanel.add(logoPanel, BorderLayout.NORTH);

        // 2. Navigation Panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(30, 30, 30));
        navPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        navButtons = new JButton[navTabNames.length];
        for (int i = 0; i < navTabNames.length; i++) {
            final String tab = navTabNames[i];
            JButton btn = new JButton(tab);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setPreferredSize(new Dimension(190, 40));
            btn.setBackground(new Color(30, 30, 30));
            btn.setForeground(new Color(180, 180, 180));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.putClientProperty("JButton.buttonType", "roundRect");

            btn.addActionListener(e -> showTab(tab));

            navButtons[i] = btn;
            navPanel.add(btn);
            navPanel.add(Box.createRigidArea(new Dimension(0, 6))); // small gap
        }

        sidebarPanel.add(navPanel, BorderLayout.CENTER);

        // 3. Footer / User Info Panel (SOUTH)
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBackground(new Color(30, 30, 30));
        southPanel.setBorder(new EmptyBorder(10, 15, 20, 15));

        // User info mini card
        JPanel userCard = new JPanel(new GridLayout(2, 1, 0, 3));
        userCard.setBackground(new Color(25, 25, 25));
        userCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 45)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        lblUserAccount = new JLabel("@" + username);
        lblUserAccount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserAccount.setForeground(Color.WHITE);
        userCard.add(lblUserAccount);

        lblUserBalance = new JLabel(priceFormat.format(portfolioController.getCashBalance()));
        lblUserBalance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUserBalance.setForeground(new Color(46, 204, 113));
        userCard.add(lblUserBalance);

        southPanel.add(userCard);
        southPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Logout Button
        JButton btnLogout = new JButton("Log Out");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(192, 57, 43)); // Red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnLogout.setPreferredSize(new Dimension(190, 35));
        btnLogout.putClientProperty("JButton.buttonType", "roundRect");
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> handleLogout());
        southPanel.add(btnLogout);

        sidebarPanel.add(southPanel, BorderLayout.SOUTH);
    }

    private void showTab(String tabName) {
        // 1. Unregister observers
        if (dashboardPanel != null) dashboardPanel.unregisterMarketObserver();
        if (portfolioPanel != null) portfolioPanel.unregisterMarketObserver();
        if (watchlistPanel != null) watchlistPanel.unregisterMarketObserver();

        // 2. Style buttons to show selected tab
        if (navButtons != null) {
            for (int i = 0; i < navTabNames.length; i++) {
                if (navTabNames[i].equalsIgnoreCase(tabName)) {
                    navButtons[i].setBackground(new Color(46, 204, 113)); // emerald green
                    navButtons[i].setForeground(Color.WHITE);
                } else {
                    navButtons[i].setBackground(new Color(30, 30, 30));
                    navButtons[i].setForeground(new Color(180, 180, 180));
                }
            }
        }

        // 3. Register observer and refresh pages
        if ("Dashboard".equalsIgnoreCase(tabName)) {
            dashboardPanel.registerMarketObserver();
            if (lblPageTitle != null) lblPageTitle.setText("MARKET DASHBOARD");
        } else if ("Portfolio".equalsIgnoreCase(tabName)) {
            portfolioPanel.refreshPortfolioDetails();
            portfolioPanel.registerMarketObserver();
            if (lblPageTitle != null) lblPageTitle.setText("MY PORTFOLIO TERMINAL");
        } else if ("Watchlist".equalsIgnoreCase(tabName)) {
            watchlistPanel.refreshWatchlist();
            watchlistPanel.registerMarketObserver();
            if (lblPageTitle != null) lblPageTitle.setText("MY FAVORITE WATCHLIST");
        } else if ("Transactions".equalsIgnoreCase(tabName)) {
            transactionPanel.refresh();
            if (lblPageTitle != null) lblPageTitle.setText("TRANSACTION LEDGER HISTORY");
        } else if ("Leaderboard".equalsIgnoreCase(tabName)) {
            leaderboardPanel.refresh();
            if (lblPageTitle != null) lblPageTitle.setText("INVESTOR LEADERBOARD STANDINGS");
        } else if ("StonBot".equalsIgnoreCase(tabName)) {
            if (lblPageTitle != null) lblPageTitle.setText("STONBOT AI INVESTING ASSISTANT");
        }

        // Refresh cash balance display in sidebar
        refreshSidebar();

        // Swap panel card
        mainCardLayout.show(mainCardPanel, tabName);
    }

    public void refreshSidebar() {
        if (lblUserBalance != null) {
            lblUserBalance.setText(priceFormat.format(portfolioController.getCashBalance()));
        }
    }

    public void refreshDashboardPanel() {
        if (dashboardPanel != null) {
            dashboardPanel.onStockUpdate(stockController.getAllStocks());
        }
    }

    public void refreshWatchlistPanel() {
        if (watchlistPanel != null) {
            watchlistPanel.refreshWatchlist();
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of StonX?", "Confirm Log Out",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Remove frame as observer of simulator
            stockController.removeObserver(this);

            userController.logout();

            // Clear panel references
            dashboardPanel = null;
            portfolioPanel = null;
            watchlistPanel = null;
            transactionPanel = null;
            leaderboardPanel = null;
            stonBotPanel = null;

            showLoggedOutLayout();
        }
    }

    public void showPanel(String cardName) {
        mainCardLayout.show(mainCardPanel, cardName);
    }

    @Override
    public void onStockUpdate(List<Stock> stocks) {
        SwingUtilities.invokeLater(() -> {
            if (footerTicker != null) {
                footerTicker.repaint();
            }
        });
    }

    /**
     * Footer Horizontal Scrolling Stock ribbon.
     */
    private class FooterTickerRibbon extends JPanel {
        private int offset = 0;
        private final Timer timer;

        public FooterTickerRibbon() {
            setPreferredSize(new Dimension(0, 32));
            setBackground(new Color(25, 25, 25));
            setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(45, 45, 45)));

            timer = new Timer(30, e -> {
                offset -= 1;
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
            g2.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));

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

                    g2.setColor(new Color(150, 150, 150));
                    g2.drawString(sym + "  ", x, 20);
                    x += g2.getFontMetrics().stringWidth(sym + "  ");

                    g2.setColor(Color.WHITE);
                    g2.drawString(priceFormat.format(price) + "  ", x, 20);
                    x += g2.getFontMetrics().stringWidth(priceFormat.format(price) + "  ");

                    if (change >= 0) {
                        g2.setColor(new Color(46, 204, 113));
                    } else {
                        g2.setColor(new Color(231, 76, 60));
                    }
                    g2.drawString(changeText + "   |   ", x, 20);
                    x += g2.getFontMetrics().stringWidth(changeText + "   |   ");
                }
            }
            g2.dispose();
        }
    }
}
