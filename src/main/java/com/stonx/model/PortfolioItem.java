package com.stonx.model;

/**
 * Demonstrates Encapsulation.
 * Tracks user holdings for a specific stock ticker.
 */
public class PortfolioItem {
    private final String symbol;
    private int quantity;
    private double averageBuyPrice;

    public PortfolioItem(String symbol, int quantity, double averageBuyPrice) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be empty.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        if (averageBuyPrice < 0) {
            throw new IllegalArgumentException("Average buy price cannot be negative.");
        }
        this.symbol = symbol.toUpperCase().trim();
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public synchronized int getQuantity() {
        return quantity;
    }

    public synchronized double getAverageBuyPrice() {
        return averageBuyPrice;
    }

    /**
     * Updates holdings when buying more shares.
     * Computes the new weighted average buy price.
     */
    public synchronized void buy(int addQty, double buyPrice) {
        if (addQty <= 0) {
            throw new IllegalArgumentException("Purchase quantity must be greater than zero.");
        }
        double totalCost = (this.quantity * this.averageBuyPrice) + (addQty * buyPrice);
        this.quantity += addQty;
        this.averageBuyPrice = totalCost / this.quantity;
    }

    /**
     * Updates holdings when selling shares.
     * Returns true if sold successfully, false if not enough shares.
     */
    public synchronized boolean sell(int sellQty) {
        if (sellQty <= 0) {
            throw new IllegalArgumentException("Sell quantity must be greater than zero.");
        }
        if (sellQty > this.quantity) {
            return false;
        }
        this.quantity -= sellQty;
        return true;
    }
}
