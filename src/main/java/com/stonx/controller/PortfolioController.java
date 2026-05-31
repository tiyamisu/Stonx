package com.stonx.controller;

import com.stonx.model.PortfolioItem;
import com.stonx.model.Transaction;
import com.stonx.model.User;
import com.stonx.service.StockMarketServiceImpl;
import com.stonx.service.UserService;
import com.stonx.utils.FileHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller part of MVC architecture.
 * Mediates buying/selling operations and portfolio valuation summaries.
 */
public class PortfolioController {
    private final UserService userService;

    public PortfolioController() {
        this.userService = UserService.getInstance();
    }

    public boolean buyStock(String symbol, int quantity) {
        User user = userService.getCurrentUser();
        if (user == null) return false;
        
        // Get live price
        var stock = StockMarketServiceImpl.getInstance().getStock(symbol);
        if (stock == null) return false;

        double livePrice = stock.getCurrentPrice();
        return userService.executeTrade(symbol, quantity, "BUY", livePrice);
    }

    public boolean sellStock(String symbol, int quantity) {
        User user = userService.getCurrentUser();
        if (user == null) return false;

        // Get live price
        var stock = StockMarketServiceImpl.getInstance().getStock(symbol);
        if (stock == null) return false;

        double livePrice = stock.getCurrentPrice();
        return userService.executeTrade(symbol, quantity, "SELL", livePrice);
    }

    public double getCashBalance() {
        User user = userService.getCurrentUser();
        return user != null ? user.getBalance() : 0.0;
    }

    public double getPortfolioValue() {
        User user = userService.getCurrentUser();
        if (user == null) return 0.0;
        Map<String, Double> prices = StockMarketServiceImpl.getInstance().getStockPrices();
        return user.getPortfolio().getPortfolioValue(prices);
    }

    public double getPortfolioInvested() {
        User user = userService.getCurrentUser();
        if (user == null) return 0.0;
        return user.getPortfolio().getTotalInvested();
    }

    public double getPortfolioProfitLoss() {
        User user = userService.getCurrentUser();
        if (user == null) return 0.0;
        Map<String, Double> prices = StockMarketServiceImpl.getInstance().getStockPrices();
        return user.getPortfolio().getProfitOrLoss(prices);
    }

    public double getPortfolioReturnPercent() {
        User user = userService.getCurrentUser();
        if (user == null) return 0.0;
        Map<String, Double> prices = StockMarketServiceImpl.getInstance().getStockPrices();
        return user.getPortfolio().getPercentageReturn(prices);
    }

    public List<PortfolioItem> getHoldings() {
        User user = userService.getCurrentUser();
        return user != null ? user.getPortfolio().getHoldingsList() : Collections.emptyList();
    }

    public List<Transaction> getTransactionHistory() {
        User user = userService.getCurrentUser();
        if (user == null) return Collections.emptyList();
        return FileHandler.loadTransactions(user.getUsername());
    }
}
