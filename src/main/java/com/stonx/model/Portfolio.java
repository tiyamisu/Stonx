package com.stonx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates Encapsulation & Polymorphism (via collection management).
 * Manages the collection of stock holdings for a user.
 */
public class Portfolio {
    private final Map<String, PortfolioItem> holdings = new ConcurrentHashMap<>();

    /**
     * Records a buy trade in the portfolio.
     */
    public synchronized void recordBuy(String symbol, int qty, double price) {
        String key = symbol.toUpperCase().trim();
        if (holdings.containsKey(key)) {
            holdings.get(key).buy(qty, price);
        } else {
            holdings.put(key, new PortfolioItem(key, qty, price));
        }
    }

    /**
     * Records a sell trade. Returns true if trade succeeded, false if insufficient shares.
     */
    public synchronized boolean recordSell(String symbol, int qty) {
        String key = symbol.toUpperCase().trim();
        if (!holdings.containsKey(key)) {
            return false;
        }
        PortfolioItem item = holdings.get(key);
        boolean success = item.sell(qty);
        if (success && item.getQuantity() == 0) {
            holdings.remove(key); // Remove item if no shares left
        }
        return success;
    }

    public synchronized List<PortfolioItem> getHoldingsList() {
        return new ArrayList<>(holdings.values());
    }

    public synchronized PortfolioItem getHolding(String symbol) {
        return holdings.get(symbol.toUpperCase().trim());
    }

    public synchronized void clear() {
        holdings.clear();
    }

    /**
     * Calculates the total current valuation of the holdings based on current market prices.
     *
     * @param marketPrices a map containing current prices of all symbols
     */
    public synchronized double getPortfolioValue(Map<String, Double> marketPrices) {
        double totalVal = 0.0;
        for (PortfolioItem item : holdings.values()) {
            double currentPrice = marketPrices.getOrDefault(item.getSymbol(), item.getAverageBuyPrice());
            totalVal += item.getQuantity() * currentPrice;
        }
        return totalVal;
    }

    /**
     * Calculates the total invested amount (average purchase cost).
     */
    public synchronized double getTotalInvested() {
        double totalInvested = 0.0;
        for (PortfolioItem item : holdings.values()) {
            totalInvested += item.getQuantity() * item.getAverageBuyPrice();
        }
        return totalInvested;
    }

    /**
     * Calculates the absolute profit or loss of the portfolio.
     */
    public synchronized double getProfitOrLoss(Map<String, Double> marketPrices) {
        return getPortfolioValue(marketPrices) - getTotalInvested();
    }

    /**
     * Calculates the percentage return.
     */
    public synchronized double getPercentageReturn(Map<String, Double> marketPrices) {
        double invested = getTotalInvested();
        if (invested == 0) return 0.0;
        return (getProfitOrLoss(marketPrices) / invested) * 100.0;
    }
}
