package com.stonx.utils;

/**
 * Design Pattern: Observer Pattern.
 * Interface representing the subject that observers register with.
 */
public interface StockSubject {
    void registerObserver(StockObserver observer);
    void removeObserver(StockObserver observer);
    void notifyObservers();
}
