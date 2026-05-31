package com.stonx;

import com.formdev.flatlaf.FlatDarkLaf;
import com.stonx.model.User;
import com.stonx.ui.MainFrame;
import com.stonx.utils.FileHandler;

import javax.swing.*;
import java.util.List;

/**
 * Entry point for the StonX virtual stock simulator application.
 */
public class App {
    public static void main(String[] args) {
        // Setup FlatLaf Dark Look and Feel
        try {
            FlatDarkLaf.setup();
            // Optional custom UI configurations to match modern design guidelines
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 12);
        } catch (Exception e) {
            System.err.println("FlatLaf initialization failed. Falling back to default system theme.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Initialize database files with mock data if they are empty
        initializeMockDataIfRequired();

        // Launch Application on Event Dispatch Thread (Swing safety)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    /**
     * Self-healing initial data setup.
     * Seeds the local databases with mock Indian celebrity investors and portfolios to populate the leaderboard.
     * Ensures java_demo/1234 exists as default login credentials.
     */
    private static void initializeMockDataIfRequired() {
        List<User> users = FileHandler.loadUsers();
        boolean hasDemo = false;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase("java_demo")) {
                hasDemo = true;
                break;
            }
        }

        if (!hasDemo) {
            System.out.println("Seeding default 'java_demo' account for faculty presentation...");

            // Create java_demo user
            User demo = new User("java_demo", "1234", 100000.0);
            demo.getPortfolio().recordBuy("RELIANCE", 20, 2430.0);
            demo.getPortfolio().recordBuy("TCS", 10, 3310.0);
            demo.addToWatchlist("RELIANCE");
            demo.addToWatchlist("TCS");
            demo.addToWatchlist("INFY");

            users.add(demo);
            FileHandler.saveOrUpdateUser(demo);

            // Seed other celebrity users if data is empty
            if (users.size() == 1) {
                User u1 = new User("Rakesh_Jhunjhunwala", "pass123", 550000.0);
                User u2 = new User("Radhakishan_Damani", "pass123", 420000.0);
                User u3 = new User("Vijay_Kedia", "pass123", 280000.0);
                User u4 = new User("Faculty_Evaluator", "admin123", 100000.0);

                u1.getPortfolio().recordBuy("RELIANCE", 100, 2410.0);
                u1.getPortfolio().recordBuy("TCS", 50, 3320.0);
                u1.getPortfolio().recordBuy("ADANIPORTS", 200, 790.0);

                u2.getPortfolio().recordBuy("HDFCBANK", 120, 1630.0);
                u2.getPortfolio().recordBuy("ICICIBANK", 180, 910.0);

                u3.getPortfolio().recordBuy("TATAMOTORS", 300, 610.0);
                u3.getPortfolio().recordBuy("WIPRO", 250, 405.0);

                u4.getPortfolio().recordBuy("INFY", 10, 1515.0);
                u4.getPortfolio().recordBuy("SBIN", 30, 575.0);

                u1.addToWatchlist("RELIANCE");
                u1.addToWatchlist("TCS");
                u4.addToWatchlist("RELIANCE");
                u4.addToWatchlist("INFY");
                u4.addToWatchlist("SBIN");

                FileHandler.saveOrUpdateUser(u1);
                FileHandler.saveOrUpdateUser(u2);
                FileHandler.saveOrUpdateUser(u3);
                FileHandler.saveOrUpdateUser(u4);

                users.add(u1);
                users.add(u2);
                users.add(u3);
                users.add(u4);
            }

            FileHandler.saveAllPortfolios(users);
            FileHandler.saveAllWatchlists(users);

            // Record some seed transactions
            FileHandler.saveTransaction(new com.stonx.model.Transaction("java_demo", "2026-05-31 10:00:00", "RELIANCE", "BUY", 20, 2430.0));
            FileHandler.saveTransaction(new com.stonx.model.Transaction("java_demo", "2026-05-31 10:05:00", "TCS", "BUY", 10, 3310.0));
            FileHandler.saveTransaction(new com.stonx.model.Transaction("Rakesh_Jhunjhunwala", "2026-05-30 10:15:30", "RELIANCE", "BUY", 100, 2410.0));
            FileHandler.saveTransaction(new com.stonx.model.Transaction("Rakesh_Jhunjhunwala", "2026-05-30 11:22:15", "TCS", "BUY", 50, 3320.0));
            FileHandler.saveTransaction(new com.stonx.model.Transaction("Radhakishan_Damani", "2026-05-30 14:05:40", "HDFCBANK", "BUY", 120, 1630.0));
            FileHandler.saveTransaction(new com.stonx.model.Transaction("Vijay_Kedia", "2026-05-30 15:45:00", "TATAMOTORS", "BUY", 300, 610.0));
            FileHandler.saveTransaction(new com.stonx.model.Transaction("Faculty_Evaluator", "2026-05-31 09:30:00", "INFY", "BUY", 10, 1515.0));

            System.out.println("Seed completed successfully!");
        }
    }
}
