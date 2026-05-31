package com.stonx.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Demonstrates Encapsulation.
 * Represents a registered virtual trader on the StonX platform.
 */
public class User {
    private final String username;
    private String passwordHash; // In a production app, we would hash it. In our simulator, we store hashed/plain.
    private double balance;
    private final Portfolio portfolio;
    private final List<String> watchlist;

    public User(String username, String passwordHash, double startingBalance) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        this.username = username.trim();
        this.passwordHash = passwordHash;
        this.balance = startingBalance;
        this.portfolio = new Portfolio();
        this.watchlist = Collections.synchronizedList(new ArrayList<>());
    }

    public String getUsername() {
        return username;
    }

    public synchronized String getPasswordHash() {
        return passwordHash;
    }

    public synchronized void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public synchronized void updateBalance(double amount) {
        this.balance += amount;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public List<String> getWatchlist() {
        synchronized (watchlist) {
            return new ArrayList<>(watchlist);
        }
    }

    public synchronized void addToWatchlist(String symbol) {
        String cleanSymbol = symbol.toUpperCase().trim();
        synchronized (watchlist) {
            if (!watchlist.contains(cleanSymbol)) {
                watchlist.add(cleanSymbol);
            }
        }
    }

    public synchronized void removeFromWatchlist(String symbol) {
        String cleanSymbol = symbol.toUpperCase().trim();
        synchronized (watchlist) {
            watchlist.remove(cleanSymbol);
        }
    }

    public synchronized boolean isWatching(String symbol) {
        String cleanSymbol = symbol.toUpperCase().trim();
        synchronized (watchlist) {
            return watchlist.contains(cleanSymbol);
        }
    }

    /**
     * Dynamically calculates a performance score from 0-100.
     */
    public synchronized int calculateInvestorScore(java.util.Map<String, Double> prices) {
        double portfolioValue = portfolio.getPortfolioValue(prices);
        double netWorth = balance + portfolioValue;
        double returnPct = ((netWorth - 100000.0) / 100000.0) * 100.0;

        int score = 50; // base score
        score += (int) (returnPct * 1.5); // 1.5 points per 1% return
        
        int holdingsCount = portfolio.getHoldingsList().size();
        score += holdingsCount * 12; // 12 points per unique stock held

        return Math.max(5, Math.min(100, score));
    }

    /**
     * Compiles unlocked badges based on investor data.
     */
    public synchronized List<String> getAchievements(java.util.Map<String, Double> prices) {
        List<String> list = new ArrayList<>();
        double portfolioValue = portfolio.getPortfolioValue(prices);
        double netWorth = balance + portfolioValue;
        double returnPct = ((netWorth - 100000.0) / 100000.0) * 100.0;

        int holdingsCount = portfolio.getHoldingsList().size();
        if (holdingsCount > 0) {
            list.add("First Trade 🥇");
        }
        if (holdingsCount >= 3) {
            list.add("Diversified 🛡️");
        }
        if (netWorth >= 120000.0) {
            list.add("Wealthy 💰");
        }
        if (returnPct >= 3.0) {
            list.add("Bull Rider 🐂");
        }
        
        if (list.isEmpty()) {
            list.add("Novice 🌱");
        }
        return list;
    }
}
