package com.stonx.model;

/**
 * Demonstrates Abstraction & Encapsulation.
 * Abstract class representing a general financial asset.
 */
public abstract class Asset {
    // Encapsulated fields (accessible via getters)
    private final String symbol;
    private final String name;

    public Asset(String symbol, String name) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Asset symbol cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Asset name cannot be null or empty.");
        }
        this.symbol = symbol.toUpperCase().trim();
        this.name = name.trim();
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    /**
     * Abstract method demonstrating Abstraction.
     * Subclasses must provide their own implementation of how value is calculated.
     *
     * @return current value of the asset
     */
    public abstract double getCurrentValue();
}
