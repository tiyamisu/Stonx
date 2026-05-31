package com.stonx.model;

/**
 * Demonstrates Encapsulation.
 * Immutable class representing a completed stock transaction.
 */
public class Transaction {
    private final String username;
    private final String timestamp;
    private final String symbol;
    private final String type; // BUY or SELL
    private final int quantity;
    private final double price;
    private final double totalAmount;

    public Transaction(String username, String timestamp, String symbol, String type, int quantity, double price) {
        this.username = username;
        this.timestamp = timestamp;
        this.symbol = symbol.toUpperCase().trim();
        this.type = type.toUpperCase().trim();
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = quantity * price;
    }

    public String getUsername() {
        return username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
