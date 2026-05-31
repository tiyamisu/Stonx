package com.stonx.ui;

import com.stonx.controller.PortfolioController;
import com.stonx.controller.StockController;
import com.stonx.model.PortfolioItem;
import com.stonx.model.Stock;
import com.stonx.model.User;
import com.stonx.ui.components.ModernCardPanel;
import com.stonx.utils.StockObserver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Portfolio tracking view.
 * Displays summary cards (Cash, Equity, Net Assets, returns) and details of holdings.
 * Features an interactive Gamification panel tracking Investor Score and Badge Achievements.
 */
public class PortfolioPanel extends JPanel implements StockObserver {
    private final MainFrame mainFrame;
    private final PortfolioController portfolioController;
    private final StockController stockController;

    private JLabel lblCash;
    private JLabel lblEquity;
    private JLabel lblTotalAssets;
    private JLabel lblReturns;

    private JTable tblHoldings;
    private DefaultTableModel tableModel;

    // Gamification components
    private JLabel lblInvestorScore;
    private JProgressBar barInvestorScore;
    private JLabel lblScoreRating;
    private JPanel panelBadges;

    private final DecimalFormat priceFormat = new DecimalFormat("₹#,##0.00");
    private final DecimalFormat percentFormat = new DecimalFormat("+#,##0.00%;-#,##0.00%");

    public PortfolioPanel(MainFrame mainFrame, PortfolioController portCtrl, StockController stockCtrl) {
        this.mainFrame = mainFrame;
        this.portfolioController = portCtrl;
        this.stockController = stockCtrl;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(20, 20, 20));

        initComponents();
        refreshPortfolioDetails();
    }

    private void initComponents() {
        // --- TOP: USER SUMMARY CARDS ---
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryPanel.setBackground(new Color(20, 20, 20));
        summaryPanel.setPreferredSize(new Dimension(0, 75));

        lblCash = createSummaryCard(summaryPanel, "CASH BALANCE", "₹100,000.00", new Color(155, 89, 182)); // Purple
        lblEquity = createSummaryCard(summaryPanel, "PORTFOLIO VALUE", "₹0.00", new Color(52, 152, 219));    // Blue
        lblTotalAssets = createSummaryCard(summaryPanel, "NET WORTH", "₹100,000.00", new Color(241, 196, 15)); // Gold
        lblReturns = createSummaryCard(summaryPanel, "TOTAL RETURNS (P&L)", "₹0.00 (0.00%)", new Color(46, 204, 113)); // Green

        add(summaryPanel, BorderLayout.NORTH);

        // --- CENTER: SPLIT HOLDINGS TABLE & INSIGHTS ---
        JPanel leftHoldings = new JPanel(new BorderLayout());
        leftHoldings.setBackground(new Color(25, 25, 25));
        leftHoldings.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 45)),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblTitle = new JLabel("Current Portfolio Holdings");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        leftHoldings.add(lblTitle, BorderLayout.NORTH);

        // Holdings Table Model
        String[] columnNames = {"Symbol", "Shares Held", "Avg Buy Price", "Current Price", "Invested Value", "Current Value", "Profit / Loss"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoldings = new JTable(tableModel);
        tblHoldings.setRowHeight(38);
        tblHoldings.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblHoldings.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblHoldings.setShowGrid(false);
        tblHoldings.setIntercellSpacing(new Dimension(0, 0));

        tblHoldings.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(noFocusBorder);

                if (isSelected) {
                    c.setBackground(new Color(45, 52, 54));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(new Color(25, 25, 25));
                    c.setForeground(new Color(220, 220, 220));
                }

                // Highlight Profit/Loss (column index 6)
                if (column == 6) {
                    String strVal = (String) value;
                    if (strVal.startsWith("-")) {
                        c.setForeground(new Color(231, 76, 60));
                    } else if (strVal.startsWith("+") || !strVal.contains("0.00")) {
                        c.setForeground(new Color(46, 204, 113));
                    } else {
                        c.setForeground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollHoldings = new JScrollPane(tblHoldings);
        scrollHoldings.setBorder(BorderFactory.createEmptyBorder());
        scrollHoldings.getViewport().setBackground(new Color(25, 25, 25));
        leftHoldings.add(scrollHoldings, BorderLayout.CENTER);

        // --- RIGHT COLUMN: INSIGHTS & GAMIFICATION ---
        ModernCardPanel rightInsights = new ModernCardPanel(16);
        rightInsights.setHoverEnabled(false);
        rightInsights.setLayout(new GridBagLayout());
        rightInsights.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Title
        JLabel lblInsightsTitle = new JLabel("Simulated Investor Insights");
        lblInsightsTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblInsightsTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        rightInsights.add(lblInsightsTitle, gbc);

        // Score Label Box
        JPanel panelScoreBox = new JPanel(new BorderLayout(10, 5));
        panelScoreBox.setOpaque(false);
        panelScoreBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 45)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        panelScoreBox.setBackground(new Color(30, 30, 30));

        JLabel lblScoreTitle = new JLabel("Investor Performance Score");
        lblScoreTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblScoreTitle.setForeground(new Color(150, 150, 150));
        panelScoreBox.add(lblScoreTitle, BorderLayout.NORTH);

        lblInvestorScore = new JLabel("50 / 100");
        lblInvestorScore.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblInvestorScore.setForeground(new Color(46, 204, 113));
        panelScoreBox.add(lblInvestorScore, BorderLayout.CENTER);

        lblScoreRating = new JLabel("Novice Investor 🌱");
        lblScoreRating.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblScoreRating.setForeground(Color.WHITE);
        panelScoreBox.add(lblScoreRating, BorderLayout.EAST);

        barInvestorScore = new JProgressBar(0, 100);
        barInvestorScore.setValue(50);
        barInvestorScore.setForeground(new Color(46, 204, 113));
        barInvestorScore.setBackground(new Color(45, 45, 45));
        barInvestorScore.setPreferredSize(new Dimension(0, 6));
        panelScoreBox.add(barInvestorScore, BorderLayout.SOUTH);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        rightInsights.add(panelScoreBox, gbc);

        // Achievements Badge Title
        JLabel lblBadgesTitle = new JLabel("Simulator Achievement Badges");
        lblBadgesTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBadgesTitle.setForeground(Color.WHITE);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        rightInsights.add(lblBadgesTitle, gbc);

        // Badges Flow List
        panelBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panelBadges.setOpaque(false);
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        rightInsights.add(panelBadges, gbc);

        // Assemble Left Holdings and Right Insights in Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftHoldings, rightInsights);
        splitPane.setDividerLocation(660);
        splitPane.setDividerSize(5);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        add(splitPane, BorderLayout.CENTER);
    }

    private JLabel createSummaryCard(JPanel parentPanel, String title, String value, Color accentColor) {
        ModernCardPanel card = new ModernCardPanel(12);
        card.setHoverEnabled(true);
        card.setAccentStripColor(accentColor);
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 4));
        inner.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(new Color(150, 150, 150));
        inner.add(lblTitle);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVal.setForeground(Color.WHITE);
        inner.add(lblVal);

        card.add(inner, BorderLayout.CENTER);
        parentPanel.add(card);
        return lblVal;
    }

    /**
     * Refreshes portfolio details and calculates achievements dynamically.
     */
    public synchronized void refreshPortfolioDetails() {
        double balance = portfolioController.getCashBalance();
        double pValue = portfolioController.getPortfolioValue();
        double pnl = portfolioController.getPortfolioProfitLoss();
        double returns = portfolioController.getPortfolioReturnPercent();

        lblCash.setText(priceFormat.format(balance));
        lblEquity.setText(priceFormat.format(pValue));
        lblTotalAssets.setText(priceFormat.format(balance + pValue));

        String pnlSymbol = pnl >= 0 ? "+" : "";
        lblReturns.setText(pnlSymbol + priceFormat.format(pnl) + " (" + formatPercent(returns) + ")");
        if (pnl >= 0) {
            lblReturns.setForeground(new Color(46, 204, 113));
        } else {
            lblReturns.setForeground(new Color(231, 76, 60));
        }

        // 1. Populate table holdings rows
        tableModel.setRowCount(0);
        List<PortfolioItem> items = portfolioController.getHoldings();
        for (PortfolioItem item : items) {
            Stock s = stockController.getStockDetails(item.getSymbol());
            double livePrice = s != null ? s.getCurrentPrice() : item.getAverageBuyPrice();
            double investedVal = item.getQuantity() * item.getAverageBuyPrice();
            double currentVal = item.getQuantity() * livePrice;
            double itemPnl = currentVal - investedVal;
            double itemReturn = investedVal > 0 ? (itemPnl / investedVal) * 100.0 : 0.0;

            String pnlSign = itemPnl >= 0 ? "+" : "";
            tableModel.addRow(new Object[]{
                    item.getSymbol(),
                    item.getQuantity(),
                    priceFormat.format(item.getAverageBuyPrice()),
                    priceFormat.format(livePrice),
                    priceFormat.format(investedVal),
                    priceFormat.format(currentVal),
                    pnlSign + priceFormat.format(itemPnl) + " (" + formatPercent(itemReturn) + ")"
            });
        }

        // 2. Refresh Gamification components (Investor Score & Badges)
        User user = com.stonx.service.UserService.getInstance().getCurrentUser();
        if (user != null) {
            java.util.Map<String, Double> prices = stockController.getStockPrices();
            int score = user.calculateInvestorScore(prices);
            lblInvestorScore.setText(score + " / 100");
            barInvestorScore.setValue(score);

            // Set dynamic rating text
            if (score >= 85) {
                lblScoreRating.setText("Expert Trader 🏆");
            } else if (score >= 65) {
                lblScoreRating.setText("Profitable Investor 📈");
            } else if (score >= 45) {
                lblScoreRating.setText("Intermediate Learner 📘");
            } else {
                lblScoreRating.setText("Novice Investor 🌱");
            }

            // Refresh badges layout list
            panelBadges.removeAll();
            List<String> achievements = user.getAchievements(prices);
            for (String badge : achievements) {
                JLabel badgeLabel = new JLabel(badge);
                badgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                badgeLabel.setForeground(Color.WHITE);
                badgeLabel.setBackground(new Color(45, 52, 54));
                badgeLabel.setOpaque(true);
                badgeLabel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                        new EmptyBorder(6, 12, 6, 12)
                ));
                panelBadges.add(badgeLabel);
            }
            panelBadges.revalidate();
            panelBadges.repaint();
        }
    }

    private String formatPercent(double val) {
        return percentFormat.format(val / 100.0);
    }

    @Override
    public void onStockUpdate(List<Stock> stocks) {
        SwingUtilities.invokeLater(this::refreshPortfolioDetails);
    }

    public void registerMarketObserver() {
        stockController.registerObserver(this);
    }

    public void unregisterMarketObserver() {
        stockController.removeObserver(this);
    }
}
