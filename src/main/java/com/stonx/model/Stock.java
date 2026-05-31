package com.stonx.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Demonstrates Inheritance & Polymorphism.
 * Inherits from Asset and implements getCurrentValue().
 */
public class Stock extends Asset {
    private double currentPrice;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double dailyChangePercent;
    
    // Store price history for charts (Thread-safe collection wrapper)
    private final List<Double> priceHistory = Collections.synchronizedList(new ArrayList<>());
    private static final int MAX_HISTORY_LIMIT = 30;

    public Stock(String symbol, String name, double initialPrice) {
        super(symbol, name); // Calls superclass constructor (Inheritance)
        this.currentPrice = initialPrice;
        this.openPrice = initialPrice;
        this.highPrice = initialPrice;
        this.lowPrice = initialPrice;
        this.dailyChangePercent = 0.0;
        this.priceHistory.add(initialPrice);
    }

    // Polymorphism: Custom implementation of the abstract superclass method
    @Override
    public double getCurrentValue() {
        return getCurrentPrice();
    }

    public synchronized double getCurrentPrice() {
        return currentPrice;
    }

    public synchronized void updatePrice(double newPrice) {
        if (newPrice <= 0) {
            newPrice = 0.01; // Avoid zero/negative prices
        }
        
        // Update high/low bounds
        if (newPrice > this.highPrice) {
            this.highPrice = newPrice;
        }
        if (newPrice < this.lowPrice) {
            this.lowPrice = newPrice;
        }

        this.currentPrice = newPrice;
        this.dailyChangePercent = ((newPrice - openPrice) / openPrice) * 100.0;

        // Maintain history limit
        priceHistory.add(newPrice);
        if (priceHistory.size() > MAX_HISTORY_LIMIT) {
            priceHistory.remove(0);
        }
    }

    public synchronized double getOpenPrice() {
        return openPrice;
    }

    public synchronized void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public synchronized double getHighPrice() {
        return highPrice;
    }

    public synchronized double getLowPrice() {
        return lowPrice;
    }

    public synchronized double getDailyChangePercent() {
        return dailyChangePercent;
    }

    /**
     * Returns a copy of the price history to prevent concurrent modification exceptions.
     */
    public List<Double> getPriceHistory() {
        synchronized (priceHistory) {
            return new ArrayList<>(priceHistory);
        }
    }
}
