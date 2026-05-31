package com.stonx.ui;

import com.stonx.controller.PortfolioController;
import com.stonx.controller.StockController;
import com.stonx.controller.UserController;
import com.stonx.model.Stock;
import com.stonx.ui.components.StockChartPanel;
import com.stonx.utils.StockObserver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Watchlist view.
 * Displays only stocks favorited by the logged-in user.
 * Supports charts, live updates, and trading directly from the watchlist page.
 */
public class WatchlistPanel extends JPanel implements StockObserver {
    private final MainFrame mainFrame;
    private final StockController stockController;
    private final UserController userController;
    private final PortfolioController portfolioController;

    private JTable tblWatchlist;
    private DefaultTableModel tableModel;
    private JLabel lblDetailName;
    private JLabel lblDetailSymbol;
    private JLabel lblDetailPrice;
    private JLabel lblDetailChange;

    private JSpinner spinQty;
    private JButton btnBuy;
    private JButton btnSell;
    private JButton btnRemove;

    private StockChartPanel chartPanel;
    private Stock selectedStock;

    private final DecimalFormat priceFormat = new DecimalFormat("₹#,##0.00");
    private final DecimalFormat percentFormat = new DecimalFormat("+#,##0.00%;-#,##0.00%");

    public WatchlistPanel(MainFrame mainFrame, StockController stockCtrl, UserController userCtrl, PortfolioController portCtrl) {
        this.mainFrame = mainFrame;
        this.stockController = stockCtrl;
        this.userController = userCtrl;
        this.portfolioController = portCtrl;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(20, 20, 20));

        initComponents();
        refreshWatchlist();
    }

    private void initComponents() {
        // --- LEFT COLUMN: WATCHLIST TABLE ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(25, 25, 25));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 45)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitle = new JLabel("My Watchlist");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        leftPanel.add(lblTitle, BorderLayout.NORTH);

        String[] columnNames = {"Symbol", "Company", "Price", "Change"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblWatchlist = new JTable(tableModel);
        tblWatchlist.setRowHeight(38);
        tblWatchlist.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblWatchlist.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblWatchlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblWatchlist.setShowGrid(false);
        tblWatchlist.setIntercellSpacing(new Dimension(0, 0));

        tblWatchlist.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(noFocusBorder);

                String symbol = (String) table.getValueAt(row, 0);
                Stock s = stockController.getStockDetails(symbol);

                if (isSelected) {
                    c.setBackground(new Color(45, 52, 54));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(new Color(25, 25, 25));
                    c.setForeground(new Color(220, 220, 220));
                }

                if (column == 2 || column == 3) {
                    if (s != null) {
                        if (s.getDailyChangePercent() > 0) {
                            c.setForeground(new Color(46, 204, 113));
                        } else if (s.getDailyChangePercent() < 0) {
                            c.setForeground(new Color(231, 76, 60));
                        }
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblWatchlist);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(25, 25, 25));
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        tblWatchlist.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblWatchlist.getSelectedRow();
                if (row != -1) {
                    selectStockAtRow(row);
                }
            }
        });

        // --- RIGHT COLUMN: DETAILS, CHART, QUICK TRADE ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(25, 25, 25));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 45)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Header details
        JPanel detailHeader = new JPanel(new BorderLayout());
        detailHeader.setBackground(new Color(25, 25, 25));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(new Color(25, 25, 25));

        lblDetailSymbol = new JLabel("SELECT A STOCK");
        lblDetailSymbol.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDetailSymbol.setForeground(Color.WHITE);
        titlePanel.add(lblDetailSymbol);

        lblDetailName = new JLabel("");
        lblDetailName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetailName.setForeground(new Color(150, 150, 150));
        titlePanel.add(lblDetailName);

        detailHeader.add(titlePanel, BorderLayout.WEST);

        btnRemove = new JButton("Unwatch");
        btnRemove.putClientProperty("JButton.buttonType", "roundRect");
        btnRemove.setForeground(new Color(231, 76, 60));
        btnRemove.addActionListener(e -> handleRemoveFromWatchlist());
        detailHeader.add(btnRemove, BorderLayout.EAST);

        rightPanel.add(detailHeader, BorderLayout.NORTH);

        // Chart
        chartPanel = new StockChartPanel();
        chartPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        rightPanel.add(chartPanel, BorderLayout.CENTER);

        // Footer quick trade
        JPanel detailFooter = new JPanel(new GridLayout(1, 2, 10, 0));
        detailFooter.setBackground(new Color(25, 25, 25));

        JPanel statsGrid = new JPanel(new GridLayout(1, 2, 5, 5));
        statsGrid.setBackground(new Color(25, 25, 25));

        lblDetailPrice = new JLabel("Price: ₹0.00");
        lblDetailPrice.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblDetailPrice.setForeground(Color.WHITE);

        lblDetailChange = new JLabel("Change: 0.00%");
        lblDetailChange.setFont(new Font("Segoe UI", Font.BOLD, 13));

        statsGrid.add(lblDetailPrice);
        statsGrid.add(lblDetailChange);
        detailFooter.add(statsGrid);

        JPanel tradePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        tradePanel.setBackground(new Color(25, 25, 25));

        tradePanel.add(new JLabel("Qty:"));
        spinQty = new JSpinner(new SpinnerNumberModel(5, 1, 10000, 1));
        spinQty.setPreferredSize(new Dimension(80, 28));
        tradePanel.add(spinQty);

        btnBuy = new JButton("BUY");
        btnBuy.setBackground(new Color(46, 204, 113));
        btnBuy.setForeground(Color.WHITE);
        btnBuy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuy.putClientProperty("JButton.buttonType", "roundRect");
        btnBuy.addActionListener(e -> handleTrade("BUY"));
        tradePanel.add(btnBuy);

        btnSell = new JButton("SELL");
        btnSell.setBackground(new Color(231, 76, 60));
        btnSell.setForeground(Color.WHITE);
        btnSell.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSell.putClientProperty("JButton.buttonType", "roundRect");
        btnSell.addActionListener(e -> handleTrade("SELL"));
        tradePanel.add(btnSell);

        detailFooter.add(tradePanel);
        rightPanel.add(detailFooter, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(340);
        splitPane.setDividerSize(5);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        add(splitPane, BorderLayout.CENTER);
    }

    private void selectStockAtRow(int row) {
        String symbol = (String) tableModel.getValueAt(row, 0);
        selectedStock = stockController.getStockDetails(symbol);
        refreshSelectedStockDetails();
    }

    private void refreshSelectedStockDetails() {
        if (selectedStock == null) {
            lblDetailSymbol.setText("SELECT A STOCK");
            lblDetailName.setText("");
            lblDetailPrice.setText("Price: ₹0.00");
            lblDetailChange.setText("Change: 0.00%");
            lblDetailChange.setForeground(Color.WHITE);
            chartPanel.updateChart(null);
            btnRemove.setEnabled(false);
            btnBuy.setEnabled(false);
            btnSell.setEnabled(false);
            return;
        }

        btnRemove.setEnabled(true);
        btnBuy.setEnabled(true);
        btnSell.setEnabled(true);

        lblDetailSymbol.setText(selectedStock.getSymbol());
        lblDetailName.setText(selectedStock.getName());
        lblDetailPrice.setText("Price: " + priceFormat.format(selectedStock.getCurrentPrice()));

        double change = selectedStock.getDailyChangePercent();
        lblDetailChange.setText("Change: " + formatPercent(change));
        if (change >= 0) {
            lblDetailChange.setForeground(new Color(46, 204, 113));
        } else {
            lblDetailChange.setForeground(new Color(231, 76, 60));
        }

        chartPanel.updateChart(selectedStock);
    }

    /**
     * Rebuilds watchlist rows from user profile details.
     */
    public synchronized void refreshWatchlist() {
        tableModel.setRowCount(0);
        var currentUser = userController.getCurrentUser();
        if (currentUser == null) return;

        List<String> watchlist = currentUser.getWatchlist();
        for (String symbol : watchlist) {
            Stock stock = stockController.getStockDetails(symbol);
            if (stock != null) {
                tableModel.addRow(new Object[]{
                        stock.getSymbol(),
                        stock.getName(),
                        priceFormat.format(stock.getCurrentPrice()),
                        formatPercent(stock.getDailyChangePercent())
                });
            }
        }

        // Keep selected reference correct
        if (selectedStock != null) {
            if (!currentUser.isWatching(selectedStock.getSymbol())) {
                selectedStock = null;
            } else {
                selectedStock = stockController.getStockDetails(selectedStock.getSymbol());
            }
        }

        // If nothing selected, select first item if available
        if (selectedStock == null && tblWatchlist.getRowCount() > 0) {
            tblWatchlist.setRowSelectionInterval(0, 0);
            selectStockAtRow(0);
        } else {
            refreshSelectedStockDetails();
        }
    }

    private void handleRemoveFromWatchlist() {
        if (selectedStock == null) return;
        userController.toggleWatchlist(selectedStock.getSymbol());
        refreshWatchlist();
        mainFrame.refreshDashboardPanel(); // Sync dashboard watchlist stars
    }

    private void handleTrade(String type) {
        if (selectedStock == null) return;
        int quantity = (Integer) spinQty.getValue();

        boolean success;
        if ("BUY".equalsIgnoreCase(type)) {
            success = portfolioController.buyStock(selectedStock.getSymbol(), quantity);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Successfully bought " + quantity + " shares of " + selectedStock.getSymbol() + "!",
                        "Trade Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Insufficient balance to complete purchase.",
                        "Trade Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            success = portfolioController.sellStock(selectedStock.getSymbol(), quantity);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Successfully sold " + quantity + " shares of " + selectedStock.getSymbol() + "!",
                        "Trade Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Insufficient shares in portfolio to complete sale.",
                        "Trade Failed", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (success) {
            mainFrame.refreshSidebar();
        }
    }

    private String formatPercent(double val) {
        return percentFormat.format(val / 100.0);
    }

    @Override
    public void onStockUpdate(List<Stock> stocks) {
        SwingUtilities.invokeLater(() -> {
            // Keep prices updating live in table
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String symbol = (String) tableModel.getValueAt(i, 0);
                for (Stock stock : stocks) {
                    if (stock.getSymbol().equalsIgnoreCase(symbol)) {
                        tableModel.setValueAt(priceFormat.format(stock.getCurrentPrice()), i, 2);
                        tableModel.setValueAt(formatPercent(stock.getDailyChangePercent()), i, 3);
                        break;
                    }
                }
            }

            // Keep selected details updating live
            if (selectedStock != null) {
                for (Stock s : stocks) {
                    if (s.getSymbol().equalsIgnoreCase(selectedStock.getSymbol())) {
                        selectedStock = s;
                        break;
                    }
                }
                refreshSelectedStockDetails();
            }
        });
    }

    public void registerMarketObserver() {
        stockController.registerObserver(this);
    }

    public void unregisterMarketObserver() {
        stockController.removeObserver(this);
    }
}
