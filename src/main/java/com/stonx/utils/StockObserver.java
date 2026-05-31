package com.stonx.utils;

import com.stonx.model.Stock;
import java.util.List;

/**
 * Design Pattern: Observer Pattern.
 * Interface implemented by classes that want to be notified of stock price updates.
 */
public interface StockObserver {
    /**
     * Called when the stock market updates.
     *
     * @param stocks the current list of all active stocks in the market
     */
    void onStockUpdate(List<Stock> stocks);
}
