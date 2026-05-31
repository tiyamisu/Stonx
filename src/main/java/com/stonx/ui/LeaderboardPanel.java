package com.stonx.ui;

import com.stonx.controller.UserController;
import com.stonx.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Leaderboard view.
 * Renders ranked users based on overall net asset values and displays Investor Scores.
 */
public class LeaderboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final UserController userController;

    private JTable tblLeaderboard;
    private DefaultTableModel tableModel;

    private final DecimalFormat priceFormat = new DecimalFormat("₹#,##0.00");

    public LeaderboardPanel(MainFrame mainFrame, UserController userCtrl) {
        this.mainFrame = mainFrame;
        this.userController = userCtrl;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(20, 20, 20));

        initComponents();
        refresh();
    }

    private void initComponents() {
        JPanel boardPanel = new JPanel(new BorderLayout());
        boardPanel.setBackground(new Color(25, 25, 25));
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 45)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 25));
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setBackground(new Color(25, 25, 25));

        JLabel lblTitle = new JLabel("Global Leaderboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Ranked by Net Worth (Cash + Live Stock Value) & investor proficiency rating");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(150, 150, 150));
        titlePanel.add(lblSubtitle);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Refresh Rankings");
        btnRefresh.putClientProperty("JButton.buttonType", "roundRect");
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refresh());
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        boardPanel.add(headerPanel, BorderLayout.NORTH);

        // Columns including Investor Score
        String[] columnNames = {"Rank", "Username", "Cash Balance", "Portfolio Value", "Total Net Worth", "Investor Score"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblLeaderboard = new JTable(tableModel);
        tblLeaderboard.setRowHeight(38);
        tblLeaderboard.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblLeaderboard.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblLeaderboard.setShowGrid(false);
        tblLeaderboard.setIntercellSpacing(new Dimension(0, 0));

        tblLeaderboard.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(noFocusBorder);

                String username = (String) table.getValueAt(row, 1);
                String loggedIn = userController.getCurrentUser() != null ? userController.getCurrentUser().getUsername() : "";

                if (username.equalsIgnoreCase(loggedIn)) {
                    c.setBackground(new Color(40, 55, 71)); // Dark blue highlight
                    c.setForeground(Color.WHITE);
                } else if (isSelected) {
                    c.setBackground(new Color(45, 52, 54));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(new Color(25, 25, 25));
                    c.setForeground(new Color(200, 200, 200));
                }

                if (column == 0) {
                    int rankVal = (Integer) value;
                    if (rankVal == 1) {
                        setText("🥇 1st");
                        setForeground(new Color(241, 196, 15)); // Gold
                    } else if (rankVal == 2) {
                        setText("🥈 2nd");
                        setForeground(new Color(189, 195, 199)); // Silver
                    } else if (rankVal == 3) {
                        setText("🥉 3rd");
                        setForeground(new Color(211, 84, 0)); // Bronze
                    } else {
                        setText(rankVal + "th");
                    }
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                }

                if (column == 5) {
                    // Investor Score styling
                    int scoreVal = (Integer) value;
                    setText(scoreVal + " / 100");
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    if (scoreVal >= 80) {
                        setForeground(new Color(46, 204, 113)); // expert green
                    } else if (scoreVal >= 60) {
                        setForeground(new Color(52, 152, 219)); // pro blue
                    } else {
                        setForeground(new Color(200, 200, 200));
                    }
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblLeaderboard);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(25, 25, 25));
        boardPanel.add(scrollPane, BorderLayout.CENTER);

        add(boardPanel, BorderLayout.CENTER);
    }

    /**
     * Refreshes the leaderboard statistics.
     */
    public synchronized void refresh() {
        tableModel.setRowCount(0);
        List<UserService.LeaderboardEntry> list = userController.getLeaderboard();

        int rank = 1;
        for (UserService.LeaderboardEntry entry : list) {
            tableModel.addRow(new Object[]{
                    rank++,
                    entry.getUsername(),
                    priceFormat.format(entry.getCashBalance()),
                    priceFormat.format(entry.getPortfolioValue()),
                    priceFormat.format(entry.getNetWorth()),
                    entry.getInvestorScore()
            });
        }
    }
}
