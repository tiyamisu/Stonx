package com.stonx.service;

import com.stonx.model.News;
import com.stonx.model.Stock;
import com.stonx.utils.StockObserver;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Pattern: Singleton & Observer Pattern (Subject Implementation).
 * Simulates real-time stock price changes and news events.
 */
public class StockMarketServiceImpl implements StockMarketService {
    private static StockMarketServiceImpl instance;

    private final List<Stock> stocks = new ArrayList<>();
    private final Map<String, Stock> stockMap = new HashMap<>();
    private final List<News> newsFeed = new CopyOnWriteArrayList<>();
    private final List<StockObserver> observers = new CopyOnWriteArrayList<>();

    private ScheduledExecutorService scheduler;
    private final Random random = new Random();
    
    private double marketMoodIndex = 50.0; // Starts at neutral (50)
    private News currentActiveNews = null;
    private int newsImpactTicksRemaining = 0;

    // Predefined News templates for simulation
    private final List<NewsTemplate> newsTemplates = new ArrayList<>();

    private StockMarketServiceImpl() {
        initializeStocks();
        initializeNewsTemplates();
        // Add initial welcome news
        newsFeed.add(new News("Market Opens", "StonX virtual trading session is now active. High volatility expected.", "NEUTRAL", null));
    }

    public static synchronized StockMarketServiceImpl getInstance() {
        if (instance == null) {
            instance = new StockMarketServiceImpl();
        }
        return instance;
    }

    private void initializeStocks() {
        addStock(new Stock("RELIANCE", "Reliance Industries Ltd.", 2450.0));
        addStock(new Stock("TCS", "Tata Consultancy Services Ltd.", 3350.0));
        addStock(new Stock("INFY", "Infosys Ltd.", 1520.0));
        addStock(new Stock("HDFCBANK", "HDFC Bank Ltd.", 1650.0));
        addStock(new Stock("SBIN", "State Bank of India", 580.0));
        addStock(new Stock("ICICIBANK", "ICICI Bank Ltd.", 920.0));
        addStock(new Stock("WIPRO", "Wipro Ltd.", 410.0));
        addStock(new Stock("TATAMOTORS", "Tata Motors Ltd.", 620.0));
        addStock(new Stock("HCLTECH", "HCL Technologies Ltd.", 1150.0));
        addStock(new Stock("ADANIPORTS", "Adani Ports & SEZ Ltd.", 810.0));
    }

    private void addStock(Stock stock) {
        stocks.add(stock);
        stockMap.put(stock.getSymbol(), stock);
    }

    private void initializeNewsTemplates() {
        newsTemplates.add(new NewsTemplate("Reliance reports record profits", "Reliance Q4 net profit climbs 15%, beating analysts' expectations.", "BULLISH", "RELIANCE"));
        newsTemplates.add(new NewsTemplate("Oil prices surge globally", "Global crude prices rise, impacting Reliance petrochemical margins.", "BEARISH", "RELIANCE"));
        newsTemplates.add(new NewsTemplate("TCS wins $2B mega deal", "TCS secures multi-year digital transformation contract from UK government.", "BULLISH", "TCS"));
        newsTemplates.add(new NewsTemplate("Infosys cuts annual guidance", "Infosys trims revenue growth guidance, citing slowing spending by US clients.", "BEARISH", "INFY"));
        newsTemplates.add(new NewsTemplate("IT spending slowing down", "Global headwinds reduce IT spending, affecting TCS, Infosys, and Wipro.", "BEARISH", "INFY"));
        newsTemplates.add(new NewsTemplate("HDFC Bank merger synergies kick in", "HDFC Bank records robust deposit growth post-merger integration.", "BULLISH", "HDFCBANK"));
        newsTemplates.add(new NewsTemplate("RBI increases key repo rates", "Reserve Bank of India raises rates by 25 bps, cooling banking stock gains.", "BEARISH", "HDFCBANK"));
        newsTemplates.add(new NewsTemplate("SBI quarterly asset quality improves", "SBI reports drop in non-performing assets (NPA) to a record low.", "BULLISH", "SBIN"));
        newsTemplates.add(new NewsTemplate("ICICI Bank wins digital award", "ICICI Bank's mobile app hits record downloads, driving retail loan growth.", "BULLISH", "ICICIBANK"));
        newsTemplates.add(new NewsTemplate("Tata Motors EV sales jump 40%", "Tata Motors leads Indian EV market with bumper passenger vehicle sales.", "BULLISH", "TATAMOTORS"));
        newsTemplates.add(new NewsTemplate("Tata Motors faces chip shortages", "Supply chain constraints limit commercial vehicle output for Tata Motors.", "BEARISH", "TATAMOTORS"));
        newsTemplates.add(new NewsTemplate("Wipro signs cloud deal", "Wipro partners with Microsoft for next-gen enterprise cloud products.", "BULLISH", "WIPRO"));
        newsTemplates.add(new NewsTemplate("HCLTech secures engineering order", "HCLTech bags major automotive engineering contract in Europe.", "BULLISH", "HCLTECH"));
        newsTemplates.add(new NewsTemplate("Adani Ports hits cargo milestone", "Adani Ports handles record volume of cargo across its major terminals.", "BULLISH", "ADANIPORTS"));
        newsTemplates.add(new NewsTemplate("Regulatory checks on port logistics", "Increased regulatory oversight causes minor delays at Adani Ports.", "BEARISH", "ADANIPORTS"));
    }

    @Override
    public List<Stock> getStocks() {
        return new ArrayList<>(stocks);
    }

    @Override
    public Stock getStock(String symbol) {
        return stockMap.get(symbol.toUpperCase().trim());
    }

    @Override
    public Map<String, Double> getStockPrices() {
        Map<String, Double> prices = new HashMap<>();
        for (Stock stock : stocks) {
            prices.put(stock.getSymbol(), stock.getCurrentPrice());
        }
        return prices;
    }

    @Override
    public synchronized void startSimulation() {
        if (scheduler != null && !scheduler.isShutdown()) {
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "StonX-Simulator-Thread");
            t.setDaemon(true);
            return t;
        });

        // Run simulation tick every 3 seconds
        scheduler.scheduleAtFixedRate(this::simulationTick, 0, 3, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void stopSimulation() {
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
    }

    @Override
    public List<News> getNewsFeed() {
        return new ArrayList<>(newsFeed);
    }

    @Override
    public String getMarketMood() {
        if (marketMoodIndex > 58) return "BULLISH 🐂";
        if (marketMoodIndex < 42) return "BEARISH 🐻";
        return "NEUTRAL ⚖️";
    }

    @Override
    public double getMarketMoodIndex() {
        return marketMoodIndex;
    }

    private void simulationTick() {
        try {
            // 1. Manage News Events
            if (newsImpactTicksRemaining > 0) {
                newsImpactTicksRemaining--;
                if (newsImpactTicksRemaining == 0) {
                    currentActiveNews = null;
                }
            } else {
                // 10% chance to trigger a new random news event
                if (random.nextInt(100) < 10) {
                    triggerRandomNews();
                }
            }

            // 2. Update stock prices using a Random Walk model
            int upCount = 0;
            for (Stock stock : stocks) {
                double changePercent = (random.nextDouble() * 3.0) - 1.5; // -1.5% to +1.5% random walk

                // Apply news bias if active
                if (currentActiveNews != null) {
                    String affected = currentActiveNews.getAffectedSymbol();
                    String impact = currentActiveNews.getImpactType();

                    if (affected == null || affected.equalsIgnoreCase(stock.getSymbol())) {
                        if ("BULLISH".equals(impact)) {
                            changePercent += 0.4 + random.nextDouble() * 1.2; // Add positive bias
                        } else if ("BEARISH".equals(impact)) {
                            changePercent -= (0.4 + random.nextDouble() * 1.2); // Add negative bias
                        }
                    }
                }

                double oldPrice = stock.getCurrentPrice();
                double newPrice = oldPrice * (1.0 + (changePercent / 100.0));
                
                stock.updatePrice(newPrice);
                if (newPrice > oldPrice) {
                    upCount++;
                }
            }

            // 3. Recalculate Market Mood Index
            // Simple smoothing: 80% old mood, 20% current tick performance (percent of stocks that closed positive)
            double currentTickAdvancingRatio = (double) upCount / stocks.size() * 100.0;
            marketMoodIndex = (marketMoodIndex * 0.8) + (currentTickAdvancingRatio * 0.2);
            
            // Constrain mood index between 5 and 95
            marketMoodIndex = Math.max(5.0, Math.min(95.0, marketMoodIndex));

            // 4. Notify all UI observers
            notifyObservers();
        } catch (Exception e) {
            System.err.println("Error in simulation tick: " + e.getMessage());
        }
    }

    private void triggerRandomNews() {
        NewsTemplate template = newsTemplates.get(random.nextInt(newsTemplates.size()));
        News news = new News(template.title, template.description, template.impactType, template.affectedSymbol);
        
        currentActiveNews = news;
        newsImpactTicksRemaining = 4 + random.nextInt(5); // Impact lasts 4 to 8 ticks (12 - 24 seconds)
        
        newsFeed.add(0, news); // Add to front of feed
        if (newsFeed.size() > 20) {
            newsFeed.remove(newsFeed.size() - 1);
        }
    }

    @Override
    public void triggerManualNews(News news) {
        currentActiveNews = news;
        newsImpactTicksRemaining = 6;
        newsFeed.add(0, news);
        if (newsFeed.size() > 20) {
            newsFeed.remove(newsFeed.size() - 1);
        }
        notifyObservers();
    }

    // Observer registration details
    @Override
    public void registerObserver(StockObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        List<Stock> copyOfStocks = getStocks();
        for (StockObserver observer : observers) {
            observer.onStockUpdate(copyOfStocks);
        }
    }

    // Private helper representing a news template
    private static class NewsTemplate {
        String title;
        String description;
        String impactType;
        String affectedSymbol;

        NewsTemplate(String title, String description, String impactType, String affectedSymbol) {
            this.title = title;
            this.description = description;
            this.impactType = impactType;
            this.affectedSymbol = affectedSymbol;
        }
    }
}
