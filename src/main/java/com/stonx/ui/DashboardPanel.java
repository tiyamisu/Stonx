package com.stonx.ui;

import com.stonx.controller.PortfolioController;
import com.stonx.controller.StockController;
import com.stonx.controller.UserController;
import com.stonx.model.News;
import com.stonx.model.Stock;
import com.stonx.ui.components.ModernCardPanel;
import com.stonx.ui.components.StockChartPanel;
import com.stonx.utils.StockObserver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * High-fidelity Trading Dashboard Panel.
 * Features rounded card components, JFreeChart Area chart, real-time tickers,
 * live search filtering, top movers list, and manual event triggers.
 */
public class DashboardPanel extends JPanel implements StockObserver {
    private final MainFrame mainFrame;
    private final StockController stockController;
    private final UserController userController;
    private final PortfolioController portfolioController;

    private JTable tblStocks;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;
    private JComboBox<String> comboFilter;

    // Mood Gauge
    private JLabel lblMoodText;
    private JProgressBar barMood;

    // News
    private DefaultListModel<String> newsListModel;
    private JList<String> listNews;

    // Movers Panel
    private JPanel panelMovers;

    // Stock Details card labels
    private JLabel lblDetailName;
    private JLabel lblDetailSymbol;
    private JLabel lblDetailPrice;
    private JLabel lblDetailChange;
    private JLabel lblDetailOpen;
    private JLabel lblDetailHigh;
    private JLabel lblDetailLow;

    private JSpinner spinQty;
    private JButton btnBuy;
    private JButton btnSell;
    private JButton btnWatchlist;

    private StockChartPanel chartPanel;
    private Stock selectedStock;

    private final DecimalFormat priceFormat = new DecimalFormat("₹#,##0.00");
    private final DecimalFormat percentFormat = new DecimalFormat("+#,##0.00%;-#,##0.00%");

    public DashboardPanel(MainFrame mainFrame, StockController stockCtrl, UserController userCtrl, PortfolioController portCtrl) {
        this.mainFrame = mainFrame;
        this.stockController = stockCtrl;
        this.userController = userCtrl;
        this.portfolioController = portCtrl;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(20, 20, 20));

        initComponents();

        // Default select first row
        if (tblStocks.getRowCount() > 0) {
            tblStocks.setRowSelectionInterval(0, 0);
            selectStockAtRow(0);
        }
    }

    private void initComponents() {
        // --- LEFT COLUMN: LIVE MARKET TICKERS ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(25, 25, 25));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 45)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        // Header Panel: Title + Search & Filters
        JPanel leftHeader = new JPanel(new BorderLayout(5, 5));
        leftHeader.setOpaque(false);

        JLabel lblWatchlistTitle = new JLabel("Live Market Tickers");
        lblWatchlistTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWatchlistTitle.setForeground(Color.WHITE);
        leftHeader.add(lblWatchlistTitle, BorderLayout.NORTH);

        // Search & Filter Panel
        JPanel filterPanel = new JPanel(new BorderLayout(8, 0));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Search symbol or name...");
        txtSearch.setPreferredSize(new Dimension(0, 30));
        filterPanel.add(txtSearch, BorderLayout.CENTER);

        comboFilter = new JComboBox<>(new String[]{"All Sectors", "Gainers", "Losers"});
        comboFilter.setPreferredSize(new Dimension(110, 30));
        comboFilter.addActionListener(e -> applySearchAndFilters());
        filterPanel.add(comboFilter, BorderLayout.EAST);

        leftHeader.add(filterPanel, BorderLayout.SOUTH);
        leftPanel.add(leftHeader, BorderLayout.NORTH);

        // Build JTable
        String[] columnNames = {"Symbol", "Company", "Price", "Change"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Populate initial stocks data
        List<Stock> initialStocks = stockController.getAllStocks();
        for (Stock stock : initialStocks) {
            tableModel.addRow(new Object[]{
                    stock.getSymbol(),
                    stock.getName(),
                    priceFormat.format(stock.getCurrentPrice()),
                    formatPercent(stock.getDailyChangePercent())
            });
        }

        tblStocks = new JTable(tableModel);
        tblStocks.setRowHeight(38);
        tblStocks.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblStocks.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblStocks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStocks.setShowGrid(false);
        tblStocks.setIntercellSpacing(new Dimension(0, 0));

        // Enable column sorting
        sorter = new TableRowSorter<>(tableModel);
        tblStocks.setRowSorter(sorter);

        // Document Listener for Search Bar
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applySearchAndFilters(); }
            public void removeUpdate(DocumentEvent e) { applySearchAndFilters(); }
            public void changedUpdate(DocumentEvent e) { applySearchAndFilters(); }
        });

        // Cell Renderer for colorful prices/changes
        tblStocks.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JScrollPane scrollStocks = new JScrollPane(tblStocks);
        scrollStocks.setBorder(BorderFactory.createEmptyBorder());
        scrollStocks.getViewport().setBackground(new Color(25, 25, 25));
        leftPanel.add(scrollStocks, BorderLayout.CENTER);

        // Table selection handler
        tblStocks.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblStocks.getSelectedRow();
                if (row != -1) {
                    int modelRow = tblStocks.convertRowIndexToModel(row);
                    selectStockAtRow(modelRow);
                }
            }
        });

        // --- RIGHT COLUMN: MOOD, CHART, news, triggers ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Card 1: Market Mood & Top Movers Row
        JPanel topInfoRow = new JPanel(new GridLayout(1, 2, 15, 0));
        topInfoRow.setBackground(new Color(20, 20, 20));

        // Mood panel
        ModernCardPanel moodPanel = new ModernCardPanel(12);
        moodPanel.setAccentStripColor(new Color(241, 196, 15));
        moodPanel.setLayout(new BorderLayout(5, 5));
        moodPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        moodPanel.setToolTipText("<html><b>Market Mood Index (MMI):</b><br>" +
                "Indicates general market momentum.<br>" +
                "Greed (Bullish) vs Fear (Bearish).</html>");

        JLabel lblMoodTitle = new JLabel("Market Mood Index", JLabel.LEFT);
        lblMoodTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMoodTitle.setForeground(new Color(150, 150, 150));
        moodPanel.add(lblMoodTitle, BorderLayout.NORTH);

        lblMoodText = new JLabel("NEUTRAL ⚖️", JLabel.RIGHT);
        lblMoodText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblMoodText.setForeground(Color.WHITE);
        moodPanel.add(lblMoodText, BorderLayout.EAST);

        barMood = new JProgressBar(0, 100);
        barMood.setValue(50);
        barMood.setForeground(new Color(46, 204, 113));
        barMood.setBackground(new Color(231, 76, 60));
        barMood.putClientProperty("JProgressBar.square", true);
        moodPanel.add(barMood, BorderLayout.SOUTH);
        topInfoRow.add(moodPanel);

        // Top Movers panel
        ModernCardPanel moversPanel = new ModernCardPanel(12);
        moversPanel.setAccentStripColor(new Color(155, 89, 182));
        moversPanel.setLayout(new BorderLayout());
        moversPanel.setBorder(new EmptyBorder(8, 15, 8, 15));

        JLabel lblMoversTitle = new JLabel("Live Top Gainers", JLabel.LEFT);
        lblMoversTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMoversTitle.setForeground(new Color(150, 150, 150));
        moversPanel.add(lblMoversTitle, BorderLayout.NORTH);

        panelMovers = new JPanel(new GridLayout(2, 1, 0, 4));
        panelMovers.setOpaque(false);
        moversPanel.add(panelMovers, BorderLayout.CENTER);

        topInfoRow.add(moversPanel);

        gbc.gridy = 0;
        gbc.weighty = 0.12;
        gbc.insets = new Insets(0, 0, 10, 0);
        rightPanel.add(topInfoRow, gbc);

        // Card 2: Selected Stock details & Chart Panel
        ModernCardPanel detailPanel = new ModernCardPanel(16);
        detailPanel.setHoverEnabled(false);
        detailPanel.setLayout(new BorderLayout());
        detailPanel.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Detail Header
        JPanel detailHeader = new JPanel(new BorderLayout());
        detailHeader.setOpaque(false);

        JPanel detailTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        detailTitlePanel.setOpaque(false);

        lblDetailSymbol = new JLabel("SYMBOL");
        lblDetailSymbol.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDetailSymbol.setForeground(Color.WHITE);
        detailTitlePanel.add(lblDetailSymbol);

        lblDetailName = new JLabel("Company Name Ltd.");
        lblDetailName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetailName.setForeground(new Color(150, 150, 150));
        detailTitlePanel.add(lblDetailName);

        detailHeader.add(detailTitlePanel, BorderLayout.WEST);

        // Watchlist star button
        btnWatchlist = new JButton("☆ Add to Watchlist");
        btnWatchlist.putClientProperty("JButton.buttonType", "roundRect");
        btnWatchlist.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnWatchlist.addActionListener(e -> handleWatchlistToggle());
        detailHeader.add(btnWatchlist, BorderLayout.EAST);

        detailPanel.add(detailHeader, BorderLayout.NORTH);

        // Area JFreeChart
        chartPanel = new StockChartPanel();
        chartPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
        detailPanel.add(chartPanel, BorderLayout.CENTER);

        // Stats grid + Buy/Sell Trading Panel
        JPanel detailFooter = new JPanel(new GridLayout(1, 2, 10, 0));
        detailFooter.setOpaque(false);

        // Stats panel
        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 5, 5));
        statsGrid.setOpaque(false);

        lblDetailPrice = new JLabel("Price: ₹0.00");
        lblDetailPrice.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblDetailPrice.setForeground(Color.WHITE);

        lblDetailChange = new JLabel("Change: 0.00%");
        lblDetailChange.setFont(new Font("Segoe UI", Font.BOLD, 13));

        lblDetailOpen = new JLabel("Open: ₹0.00");
        lblDetailOpen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetailOpen.setForeground(new Color(180, 180, 180));

        lblDetailHigh = new JLabel("High: ₹0.00");
        lblDetailHigh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetailHigh.setForeground(new Color(180, 180, 180));

        lblDetailLow = new JLabel("Low: ₹0.00");
        lblDetailLow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetailLow.setForeground(new Color(180, 180, 180));

        statsGrid.add(lblDetailPrice);
        statsGrid.add(lblDetailChange);
        statsGrid.add(lblDetailOpen);
        statsGrid.add(lblDetailHigh);
        statsGrid.add(lblDetailLow);

        detailFooter.add(statsGrid);

        // Trading actions panel
        JPanel tradePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        tradePanel.setOpaque(false);

        tradePanel.add(new JLabel("Qty:"));
        spinQty = new JSpinner(new SpinnerNumberModel(5, 1, 10000, 1));
        spinQty.setPreferredSize(new Dimension(80, 28));
        tradePanel.add(spinQty);

        btnBuy = new JButton("BUY");
        btnBuy.setBackground(new Color(46, 204, 113));
        btnBuy.setForeground(Color.WHITE);
        btnBuy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuy.putClientProperty("JButton.buttonType", "roundRect");
        btnBuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuy.addActionListener(e -> handleTrade("BUY"));
        tradePanel.add(btnBuy);

        btnSell = new JButton("SELL");
        btnSell.setBackground(new Color(231, 76, 60));
        btnSell.setForeground(Color.WHITE);
        btnSell.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSell.putClientProperty("JButton.buttonType", "roundRect");
        btnSell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSell.addActionListener(e -> handleTrade("SELL"));
        tradePanel.add(btnSell);

        detailFooter.add(tradePanel);
        detailPanel.add(detailFooter, BorderLayout.SOUTH);

        gbc.gridy = 1;
        gbc.weighty = 0.65;
        gbc.insets = new Insets(0, 0, 10, 0);
        rightPanel.add(detailPanel, gbc);

        // Card 3: News Feed & trigger event
        ModernCardPanel newsPanel = new ModernCardPanel(14);
        newsPanel.setHoverEnabled(false);
        newsPanel.setLayout(new BorderLayout());
        newsPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        newsPanel.setPreferredSize(new Dimension(500, 130));

        JPanel newsHeaderPanel = new JPanel(new BorderLayout());
        newsHeaderPanel.setOpaque(false);

        JLabel lblNewsTitle = new JLabel("Live Market Intelligence");
        lblNewsTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNewsTitle.setForeground(new Color(150, 150, 150));
        newsHeaderPanel.add(lblNewsTitle, BorderLayout.WEST);

        JButton btnTriggerNews = new JButton("Trigger Event ⚡");
        btnTriggerNews.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnTriggerNews.setBackground(new Color(45, 52, 54));
        btnTriggerNews.setForeground(new Color(200, 200, 200));
        btnTriggerNews.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTriggerNews.putClientProperty("JButton.buttonType", "roundRect");

        // Dynamic news triggers
        JPopupMenu newsMenu = new JPopupMenu();
        
        JMenuItem m1 = new JMenuItem("Reliance Profit Surge (+2.5% RELIANCE)");
        m1.addActionListener(e -> com.stonx.service.StockMarketServiceImpl.getInstance().triggerManualNews(
                new News("Reliance Q4 Profit Surge", "Reliance reports earnings beat, margins expand by 180bps.", "BULLISH", "RELIANCE")
        ));
        newsMenu.add(m1);

        JMenuItem m2 = new JMenuItem("Infosys Q4 Guidance Cut (-3.0% INFY)");
        m2.addActionListener(e -> com.stonx.service.StockMarketServiceImpl.getInstance().triggerManualNews(
                new News("Infosys Revenue Guidance Downgrade", "Infosys trims revenue growth forecast due to client spending slowdown.", "BEARISH", "INFY")
        ));
        newsMenu.add(m2);

        JMenuItem m3 = new JMenuItem("RBI Rate Hike (-1.5% Banks)");
        m3.addActionListener(e -> com.stonx.service.StockMarketServiceImpl.getInstance().triggerManualNews(
                new News("RBI Repo Rate Hike", "RBI increases key lending rate by 25 basis points; banking margins pressured.", "BEARISH", "HDFCBANK")
        ));
        newsMenu.add(m3);

        JMenuItem m4 = new JMenuItem("Tata Motors EV sales (+3.5% TATAMOTORS)");
        m4.addActionListener(e -> com.stonx.service.StockMarketServiceImpl.getInstance().triggerManualNews(
                new News("Tata Motors EV Shipments Double", "Tata Motors reports 45% increase in EV passenger car shipments.", "BULLISH", "TATAMOTORS")
        ));
        newsMenu.add(m4);

        JMenuItem m5 = new JMenuItem("TCS Lands $2B Deal (+2.8% TCS)");
        m5.addActionListener(e -> com.stonx.service.StockMarketServiceImpl.getInstance().triggerManualNews(
                new News("TCS Mega Government Deal", "TCS lands multi-year digital infrastructure deal with European giant.", "BULLISH", "TCS")
        ));
        newsMenu.add(m5);

        btnTriggerNews.addActionListener(e -> newsMenu.show(btnTriggerNews, 0, btnTriggerNews.getHeight()));
        newsHeaderPanel.add(btnTriggerNews, BorderLayout.EAST);
        newsPanel.add(newsHeaderPanel, BorderLayout.NORTH);

        newsListModel = new DefaultListModel<>();
        for (News n : stockController.getNewsFeed()) {
            newsListModel.addElement(n.toString() + " - " + n.getDescription());
        }

        listNews = new JList<>(newsListModel);
        listNews.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        listNews.setBackground(new Color(25, 25, 25));
        listNews.setForeground(new Color(200, 200, 200));

        JScrollPane scrollNews = new JScrollPane(listNews);
        scrollNews.setBorder(BorderFactory.createEmptyBorder());
        scrollNews.getViewport().setBackground(new Color(25, 25, 25));
        newsPanel.add(scrollNews, BorderLayout.CENTER);

        gbc.gridy = 2;
        gbc.weighty = 0.23;
        gbc.insets = new Insets(0, 0, 0, 0);
        rightPanel.add(newsPanel, gbc);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(360);
        splitPane.setDividerSize(5);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        add(splitPane, BorderLayout.CENTER);
    }

    private void applySearchAndFilters() {
        String query = txtSearch.getText().trim();
        String filter = (String) comboFilter.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!query.isEmpty()) {
            // search symbol (index 0) or company name (index 1)
            filters.add(RowFilter.regexFilter("(?i)" + query, 0, 1));
        }

        if ("Gainers".equalsIgnoreCase(filter)) {
            filters.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<?, ?> entry) {
                    String val = entry.getStringValue(3);
                    return val.startsWith("+") && !val.contains("0.00%");
                }
            });
        } else if ("Losers".equalsIgnoreCase(filter)) {
            filters.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<?, ?> entry) {
                    String val = entry.getStringValue(3);
                    return val.startsWith("-");
                }
            });
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void selectStockAtRow(int row) {
        String symbol = (String) tableModel.getValueAt(row, 0);
        selectedStock = stockController.getStockDetails(symbol);
        refreshSelectedStockDetails();
    }

    private void refreshSelectedStockDetails() {
        if (selectedStock == null) return;

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

        lblDetailOpen.setText("Open: " + priceFormat.format(selectedStock.getOpenPrice()));
        lblDetailHigh.setText("High: " + priceFormat.format(selectedStock.getHighPrice()));
        lblDetailLow.setText("Low: " + priceFormat.format(selectedStock.getLowPrice()));

        // Watchlist
        if (userController.isWatching(selectedStock.getSymbol())) {
            btnWatchlist.setText("★ Watching");
            btnWatchlist.setForeground(new Color(241, 196, 15));
        } else {
            btnWatchlist.setText("☆ Add to Watchlist");
            btnWatchlist.setForeground(Color.WHITE);
        }

        chartPanel.updateChart(selectedStock);
    }

    private void handleWatchlistToggle() {
        if (selectedStock == null) return;
        userController.toggleWatchlist(selectedStock.getSymbol());
        refreshSelectedStockDetails();
        mainFrame.refreshWatchlistPanel();
    }

    private void handleTrade(String type) {
        if (selectedStock == null) return;
        int qty = (Integer) spinQty.getValue();

        boolean success;
        if ("BUY".equalsIgnoreCase(type)) {
            success = portfolioController.buyStock(selectedStock.getSymbol(), qty);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Successfully bought " + qty + " shares of " + selectedStock.getSymbol() + "!",
                        "Trade Execution Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Trade Failed: Insufficient funds in virtual trading account.",
                        "Trade Execution Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            success = portfolioController.sellStock(selectedStock.getSymbol(), qty);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Successfully sold " + qty + " shares of " + selectedStock.getSymbol() + "!",
                        "Trade Execution Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Trade Failed: Insufficient holdings in portfolio.",
                        "Trade Execution Error", JOptionPane.ERROR_MESSAGE);
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
    public void onStockUpdate(List<Stock> updatedStocks) {
        SwingUtilities.invokeLater(() -> {
            // Update Table rows
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String symbol = (String) tableModel.getValueAt(i, 0);
                for (Stock stock : updatedStocks) {
                    if (stock.getSymbol().equalsIgnoreCase(symbol)) {
                        tableModel.setValueAt(priceFormat.format(stock.getCurrentPrice()), i, 2);
                        tableModel.setValueAt(formatPercent(stock.getDailyChangePercent()), i, 3);
                        break;
                    }
                }
            }

            // Sync selected reference
            if (selectedStock != null) {
                for (Stock s : updatedStocks) {
                    if (s.getSymbol().equalsIgnoreCase(selectedStock.getSymbol())) {
                        selectedStock = s;
                        break;
                    }
                }
                refreshSelectedStockDetails();
            }

            // Update Mood Gauge
            double moodVal = stockController.getMarketMoodIndex();
            barMood.setValue((int) moodVal);
            lblMoodText.setText(stockController.getMarketMood() + " (" + String.format("%.1f", moodVal) + ")");

            // Update Top Movers widget
            List<Stock> sorted = new ArrayList<>(updatedStocks);
            sorted.sort(Comparator.comparingDouble(Stock::getDailyChangePercent).reversed());

            panelMovers.removeAll();
            for (int i = 0; i < Math.min(2, sorted.size()); i++) {
                Stock s = sorted.get(i);
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);

                JLabel lblName = new JLabel(s.getSymbol() + "  " + s.getName());
                lblName.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblName.setForeground(new Color(220, 220, 220));

                JLabel lblPrice = new JLabel(priceFormat.format(s.getCurrentPrice()) + " (" + 
                        String.format("+%.2f%%", s.getDailyChangePercent()) + ")");
                lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblPrice.setForeground(new Color(46, 204, 113));

                row.add(lblName, BorderLayout.WEST);
                row.add(lblPrice, BorderLayout.EAST);
                panelMovers.add(row);
            }
            panelMovers.revalidate();
            panelMovers.repaint();

            // Update News feed JList
            List<News> currentNews = stockController.getNewsFeed();
            newsListModel.clear();
            for (News n : currentNews) {
                newsListModel.addElement(n.toString() + " - " + n.getDescription());
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
