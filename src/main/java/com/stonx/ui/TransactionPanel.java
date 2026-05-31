package com.stonx.ui;

import com.stonx.controller.PortfolioController;
import com.stonx.model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * View Panel for Transaction History.
 * Displays a tabular log of all purchases and sales.
 */
public class TransactionPanel extends JPanel {
    private final MainFrame mainFrame;
    private final PortfolioController portfolioController;

    private JTable tblTransactions;
    private DefaultTableModel tableModel;

    private final DecimalFormat priceFormat = new DecimalFormat("₹#,##0.00");

    public TransactionPanel(MainFrame mainFrame, PortfolioController portCtrl) {
        this.mainFrame = mainFrame;
        this.portfolioController = portCtrl;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(20, 20, 20));

        initComponents();
        refresh();
    }

    private void initComponents() {
        JPanel ledgerPanel = new JPanel(new BorderLayout());
        ledgerPanel.setBackground(new Color(25, 25, 25));
        ledgerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 45)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Header Panel with Title and Refresh button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 25));
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Transaction History Ledger");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Refresh Logs");
        btnRefresh.putClientProperty("JButton.buttonType", "roundRect");
        btnRefresh.addActionListener(e -> refresh());
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        ledgerPanel.add(headerPanel, BorderLayout.NORTH);

        // Columns for Transaction table
        String[] columnNames = {"Date & Time", "Stock Symbol", "Order Type", "Quantity", "Execution Price", "Total Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTransactions = new JTable(tableModel);
        tblTransactions.setRowHeight(35);
        tblTransactions.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblTransactions.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTransactions.setShowGrid(false);
        tblTransactions.setIntercellSpacing(new Dimension(0, 0));

        tblTransactions.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                    c.setForeground(new Color(200, 200, 200));
                }

                // Highlight BUY green and SELL red (index 2)
                if (column == 2) {
                    String type = (String) value;
                    if ("BUY".equalsIgnoreCase(type)) {
                        c.setForeground(new Color(46, 204, 113));
                    } else if ("SELL".equalsIgnoreCase(type)) {
                        c.setForeground(new Color(231, 76, 60));
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblTransactions);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(25, 25, 25));
        ledgerPanel.add(scrollPane, BorderLayout.CENTER);

        add(ledgerPanel, BorderLayout.CENTER);
    }

    /**
     * Reloads transaction events from CSV log.
     */
    public synchronized void refresh() {
        tableModel.setRowCount(0);
        List<Transaction> list = portfolioController.getTransactionHistory();
        
        // Show newest transactions first by iterating in reverse
        for (int i = list.size() - 1; i >= 0; i--) {
            Transaction tx = list.get(i);
            tableModel.addRow(new Object[]{
                    tx.getTimestamp(),
                    tx.getSymbol(),
                    tx.getType(),
                    tx.getQuantity(),
                    priceFormat.format(tx.getPrice()),
                    priceFormat.format(tx.getTotalAmount())
            });
        }
    }
}
