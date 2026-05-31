package com.stonx.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class for StonBot offline AI assistant.
 * Handles keyword matching and returns predefined financial and platform tips.
 */
public class ChatbotService {
    private static ChatbotService instance;
    private final Map<String, String> faqDatabase = new HashMap<>();

    private ChatbotService() {
        initializeDatabase();
    }

    public static synchronized ChatbotService getInstance() {
        if (instance == null) {
            instance = new ChatbotService();
        }
        return instance;
    }

    private void initializeDatabase() {
        faqDatabase.put("what is a stock?",
            "<html><b>What is a Stock?</b><br><br>" +
            "A stock (also known as equity) represents a fractional share of ownership in a corporation.<br>" +
            "When you buy a stock, you are buying a tiny piece of that company. If the company grows and becomes " +
            "more profitable, the value of your stock increases. If the company performs poorly, its stock value may drop.<br><br>" +
            "<i>In StonX:</i> You can buy shares of 10 major Indian companies like Reliance and TCS.</html>");

        faqDatabase.put("what is a portfolio?",
            "<html><b>What is a Portfolio?</b><br><br>" +
            "A portfolio is a collection of financial investments like stocks, bonds, cash, or mutual funds.<br>" +
            "Think of it as a basket of assets. Diversifying your portfolio (holding different types of stocks across " +
            "different sectors like technology, banking, and energy) helps manage risk.<br><br>" +
            "<i>In StonX:</i> Click on the 'Portfolio' tab to see your current holdings, total invested amount, and live returns.</html>");

        faqDatabase.put("what is profit/loss?",
            "<html><b>What is Profit / Loss (P&L)?</b><br><br>" +
            "Profit/Loss is the difference between the current market value of your stocks and the price you bought them for.<br>" +
            "<ul>" +
            "<li><b>Profit (Gain):</b> Occurs when the current market price is higher than your average buy price.</li>" +
            "<li><b>Loss:</b> Occurs when the current market price is lower than your average buy price.</li>" +
            "</ul>" +
            "P&L is 'unrealized' (on paper) until you actually sell the shares, at which point it becomes a 'realized' profit or loss.</html>");

        faqDatabase.put("what is a bull market?",
            "<html><b>What is a Bull Market? 🐂</b><br><br>" +
            "A bull market is a market condition where prices are rising or expected to rise.<br>" +
            "It is driven by optimism, investor confidence, and strong economic growth. The term comes from how a bull " +
            "thrusts its horns upward in an attack.<br><br>" +
            "<i>In StonX:</i> Look at the <b>Market Mood Indicator</b>. If more stocks are going up, the market shifts to Bullish!</html>");

        faqDatabase.put("what is a bear market?",
            "<html><b>What is a Bear Market? 🐻</b><br><br>" +
            "A bear market is a market condition where stock prices fall by 20% or more from recent highs, accompanied by " +
            "widespread pessimism and a slowing economy. The term comes from how a bear swipes its paws downward in an attack.<br><br>" +
            "<i>In StonX:</i> Negative news events can trigger sector sell-offs, pushing the market mood into Bearish territory. Save cash during these times!</html>");

        faqDatabase.put("how do i buy/sell stocks?",
            "<html><b>How to Trade on StonX:</b><br><br>" +
            "1. Go to the <b>Dashboard</b> tab.<br>" +
            "2. Select a stock from the table on the left (e.g., RELIANCE).<br>" +
            "3. The stock's current stats, price chart, and trading cards will appear on the right.<br>" +
            "4. Enter the quantity you want to trade.<br>" +
            "5. Click <b>BUY</b> (requires sufficient cash) or <b>SELL</b> (requires sufficient shares in portfolio).<br>" +
            "6. All transactions are logged instantly in the <b>Transactions</b> panel.</html>");

        faqDatabase.put("investing tips",
            "<html><b>Golden Rules of Investing:</b><br><br>" +
            "1. <b>Buy Low, Sell High:</b> Don't chase stocks at peak prices. Look for quality companies trading at a discount.<br>" +
            "2. <b>Diversify:</b> Don't put all your eggs in one basket. Invest across tech (TCS, INFY), banking (HDFCBANK, ICICIBANK), and energy (RELIANCE) sectors.<br>" +
            "3. <b>Keep Cash Handy:</b> Keep some buffer cash (liquidity) to buy quality stocks during market crashes.<br>" +
            "4. <b>Ignore Noise:</b> Short-term fluctuations are normal. Focus on long-term trends and company fundamentals.</html>");

        faqDatabase.put("risk management",
            "<html><b>Risk Management Strategies:</b><br><br>" +
            "1. <b>Position Sizing:</b> Avoid investing more than 10-15% of your capital in a single stock.<br>" +
            "2. <b>Understand Volatility:</b> Small-cap stocks rise and fall faster than large-caps like TCS or Reliance. Allocate capital accordingly.<br>" +
            "3. <b>Cut Losses:</b> If a stock's fundamentals deteriorate, it's better to sell at a small loss rather than holding on and suffering a huge loss.<br>" +
            "4. <b>Virtual Safety:</b> Since StonX is a simulator, test bold strategies here first before trading real money!</html>");

        faqDatabase.put("how does stonx work?",
            "<html><b>How StonX Works:</b><br><br>" +
            "StonX is a multi-threaded virtual simulation platform:<br>" +
            "<ul>" +
            "<li><b>Price Engine:</b> Simulates price changes every 3 seconds for 10 top Indian stocks.</li>" +
            "<li><b>News Engine:</b> Triggers random news bulletins that dynamically bias stock prices (bullish or bearish).</li>" +
            "<li><b>File Database:</b> Saves your balance, transactions, portfolio, and watchlists inside CSV files under the <code>data/</code> folder.</li>" +
            "<li><b>Leaderboard:</b> Ranks all registered accounts on the machine by their current combined cash and equity value.</li>" +
            "</ul>" +
            "Enjoy trading risk-free!</html>");
    }

    /**
     * Answers a query based on a keyword search or exact match.
     */
    public String answerQuery(String rawQuery) {
        if (rawQuery == null || rawQuery.trim().isEmpty()) {
            return "Please type a question or select a predefined FAQ chip below!";
        }

        String query = rawQuery.toLowerCase().trim();

        // 1. Check exact match in database
        if (faqDatabase.containsKey(query)) {
            return faqDatabase.get(query);
        }

        // 2. Keyword-based matching
        if (query.contains("what") && query.contains("stock")) {
            return faqDatabase.get("what is a stock?");
        }
        if (query.contains("portfolio") || query.contains("holdings") || query.contains("invested")) {
            return faqDatabase.get("what is a portfolio?");
        }
        if (query.contains("profit") || query.contains("loss") || query.contains("p&l") || query.contains("return")) {
            return faqDatabase.get("what is profit/loss?");
        }
        if (query.contains("bull")) {
            return faqDatabase.get("what is a bull market?");
        }
        if (query.contains("bear")) {
            return faqDatabase.get("what is a bear market?");
        }
        if (query.contains("buy") || query.contains("sell") || query.contains("trade") || query.contains("order")) {
            return faqDatabase.get("how do i buy/sell stocks?");
        }
        if (query.contains("tip") || query.contains("advice") || query.contains("rule") || query.contains("guide")) {
            return faqDatabase.get("investing tips");
        }
        if (query.contains("risk") || query.contains("diversify") || query.contains("safe")) {
            return faqDatabase.get("risk management");
        }
        if (query.contains("work") || query.contains("stonx") || query.contains("system") || query.contains("simulat")) {
            return faqDatabase.get("how does stonx work?");
        }

        // 3. Fallback response
        return "<html>" +
                "I couldn't find a direct answer to your question: \"" + rawQuery + "\"<br><br>" +
                "<b>Try asking about:</b><br>" +
                "• Stocks, Portfolios, or Profit/Loss<br>" +
                "• Bull/Bear markets<br>" +
                "• How to buy and sell stocks<br>" +
                "• Risk management or investing tips<br>" +
                "• How StonX simulates the market<br><br>" +
                "<i>Tip: You can also click the quick-question chips below!</i>" +
                "</html>";
    }
}
