package com.stonx.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Demonstrates Encapsulation.
 * Represents a simulated news event that can affect stock prices.
 */
public class News {
    private final String timestamp;
    private final String title;
    private final String description;
    private final String impactType; // BULLISH, BEARISH, NEUTRAL
    private final String affectedSymbol; // Optional: null or specific stock symbol

    public News(String title, String description, String impactType, String affectedSymbol) {
        this.timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.title = title;
        this.description = description;
        this.impactType = impactType.toUpperCase().trim();
        this.affectedSymbol = affectedSymbol != null ? affectedSymbol.toUpperCase().trim() : null;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImpactType() {
        return impactType;
    }

    public String getAffectedSymbol() {
        return affectedSymbol;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + title + (affectedSymbol != null ? " (" + affectedSymbol + ")" : "");
    }
}
