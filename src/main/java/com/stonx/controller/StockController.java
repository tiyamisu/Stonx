package com.stonx.controller;

import com.stonx.model.News;
import com.stonx.model.Stock;
import com.stonx.service.StockMarketService;
import com.stonx.service.StockMarketServiceImpl;
import com.stonx.utils.StockObserver;

import java.util.List;

/**
 * Controller part of MVC architecture.
 * Mediates communication between UI components and the StockMarketService.
 */
public class StockController {
    private final StockMarketService marketService;

    public StockController() {
        this.marketService = StockMarketServiceImpl.getInstance();
    }

    public List<Stock> getAllStocks() {
        return marketService.getStocks();
    }

    public java.util.Map<String, Double> getStockPrices() {
        return marketService.getStockPrices();
    }

    public Stock getStockDetails(String symbol) {
        return marketService.getStock(symbol);
    }

    public List<News> getNewsFeed() {
        return marketService.getNewsFeed();
    }

    public String getMarketMood() {
        return marketService.getMarketMood();
    }

    public double getMarketMoodIndex() {
        return marketService.getMarketMoodIndex();
    }

    public void startSimulation() {
        marketService.startSimulation();
    }

    public void stopSimulation() {
        marketService.stopSimulation();
    }

    public void registerObserver(StockObserver observer) {
        marketService.registerObserver(observer);
    }

    public void removeObserver(StockObserver observer) {
        marketService.removeObserver(observer);
    }
}
