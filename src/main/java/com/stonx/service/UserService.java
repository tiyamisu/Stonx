package com.stonx.service;

import com.stonx.model.PortfolioItem;
import com.stonx.model.Transaction;
import com.stonx.model.User;
import com.stonx.utils.FileHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Design Pattern: Singleton.
 * Manages accounts, sessions, execution of trades, watchlists, and leaderboards.
 */
public class UserService {
    private static UserService instance;

    private final List<User> cachedUsers = new ArrayList<>();
    private User currentUser = null;

    private UserService() {
        refreshCache();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Refreshes the user cache from file storage.
     */
    public synchronized void refreshCache() {
        cachedUsers.clear();
        cachedUsers.addAll(FileHandler.loadUsers());
        FileHandler.loadPortfolios(cachedUsers);
        FileHandler.loadWatchlists(cachedUsers);
        
        // If we have an active user, keep their session reference updated
        if (currentUser != null) {
            for (User u : cachedUsers) {
                if (u.getUsername().equalsIgnoreCase(currentUser.getUsername())) {
                    currentUser = u;
                    break;
                }
            }
        }
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized boolean login(String username, String password) {
        refreshCache();
        for (User user : cachedUsers) {
            if (user.getUsername().equalsIgnoreCase(username) && user.getPasswordHash().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public synchronized boolean register(String username, String password) {
        refreshCache();
        for (User user : cachedUsers) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return false; // User already exists
            }
        }

        User newUser = new User(username, password, 100000.0); // Starting balance ₹100,000
        cachedUsers.add(newUser);
        FileHandler.saveOrUpdateUser(newUser);
        currentUser = newUser;
        return true;
    }

    public synchronized void logout() {
        if (currentUser != null) {
            saveCurrentUserData();
            currentUser = null;
        }
    }

    public synchronized void saveCurrentUserData() {
        if (currentUser != null) {
            FileHandler.saveOrUpdateUser(currentUser);
            FileHandler.saveAllPortfolios(cachedUsers);
            FileHandler.saveAllWatchlists(cachedUsers);
        }
    }

    /**
     * Executes a stock buy or sell transaction.
     * Synchronized to prevent double-spending or race conditions.
     */
    public synchronized boolean executeTrade(String symbol, int quantity, String type, double price) {
        if (currentUser == null) return false;
        if (quantity <= 0) return false;

        String cleanType = type.toUpperCase().trim();
        double totalCost = quantity * price;

        if ("BUY".equals(cleanType)) {
            // Validate balance
            if (currentUser.getBalance() < totalCost) {
                return false; // Insufficient balance
            }
            currentUser.updateBalance(-totalCost);
            currentUser.getPortfolio().recordBuy(symbol, quantity, price);
        } else if ("SELL".equals(cleanType)) {
            // Validate holdings
            PortfolioItem holding = currentUser.getPortfolio().getHolding(symbol);
            if (holding == null || holding.getQuantity() < quantity) {
                return false; // Insufficient shares to sell
            }
            currentUser.getPortfolio().recordSell(symbol, quantity);
            currentUser.updateBalance(totalCost);
        } else {
            return false; // Invalid transaction type
        }

        // Record the transaction
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Transaction tx = new Transaction(currentUser.getUsername(), timestamp, symbol, cleanType, quantity, price);
        FileHandler.saveTransaction(tx);

        // Save current state immediately
        saveCurrentUserData();
        return true;
    }

    public synchronized void toggleWatchlist(String symbol) {
        if (currentUser == null) return;
        if (currentUser.isWatching(symbol)) {
            currentUser.removeFromWatchlist(symbol);
        } else {
            currentUser.addToWatchlist(symbol);
        }
        FileHandler.saveAllWatchlists(cachedUsers);
    }

    /**
     * Compiles leaderboard rankings of users based on their total net worth.
     */
    public synchronized List<LeaderboardEntry> getLeaderboard() {
        refreshCache();
        Map<String, Double> prices = StockMarketServiceImpl.getInstance().getStockPrices();
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        for (User user : cachedUsers) {
            double portfolioValue = user.getPortfolio().getPortfolioValue(prices);
            double netWorth = user.getBalance() + portfolioValue;
            int score = user.calculateInvestorScore(prices);
            leaderboard.add(new LeaderboardEntry(user.getUsername(), user.getBalance(), portfolioValue, netWorth, score));
        }

        // Sort by net worth in descending order
        leaderboard.sort(Comparator.comparingDouble(LeaderboardEntry::getNetWorth).reversed());
        return leaderboard;
    }

    /**
     * Inner helper class representing a leaderboard row.
     */
    public static class LeaderboardEntry {
        private final String username;
        private final double cashBalance;
        private final double portfolioValue;
        private final double netWorth;
        private final int investorScore;

        public LeaderboardEntry(String username, double cashBalance, double portfolioValue, double netWorth, int score) {
            this.username = username;
            this.cashBalance = cashBalance;
            this.portfolioValue = portfolioValue;
            this.netWorth = netWorth;
            this.investorScore = score;
        }

        public String getUsername() {
            return username;
        }

        public double getCashBalance() {
            return cashBalance;
        }

        public double getPortfolioValue() {
            return portfolioValue;
        }

        public double getNetWorth() {
            return netWorth;
        }

        public int getInvestorScore() {
            return investorScore;
        }
    }
}
