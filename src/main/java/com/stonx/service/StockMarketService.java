package com.stonx.service;

import com.stonx.model.News;
import com.stonx.model.Stock;
import com.stonx.utils.StockSubject;

import java.util.List;
import java.util.Map;

/**
 * Service interface for stock market operations.
 * Demonstrates Abstraction.
 */
public interface StockMarketService extends StockSubject {
    List<Stock> getStocks();
    Stock getStock(String symbol);
    Map<String, Double> getStockPrices();
    void startSimulation();
    void stopSimulation();
    List<News> getNewsFeed();
    String getMarketMood();
    double getMarketMoodIndex(); // 0 to 100 indicator
    void triggerManualNews(News news);
}
