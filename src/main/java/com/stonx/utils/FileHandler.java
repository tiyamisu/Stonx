package com.stonx.utils;

import com.stonx.model.Portfolio;
import com.stonx.model.PortfolioItem;
import com.stonx.model.Transaction;
import com.stonx.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles physical file read and write operations under the 'data/' folder.
 * Ensures data is persisted across application restarts.
 * Demonstrates Encapsulation and File Handling concepts.
 */
public class FileHandler {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.csv";
    private static final String PORTFOLIOS_FILE = DATA_DIR + "/portfolios.csv";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.csv";
    private static final String WATCHLIST_FILE = DATA_DIR + "/watchlists.csv";

    // Static block to initialize the data directory
    static {
        try {
            Path dirPath = Paths.get(DATA_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            createFileIfNotExist(USERS_FILE, "username,passwordHash,balance\n");
            createFileIfNotExist(PORTFOLIOS_FILE, "username,symbol,quantity,averageBuyPrice\n");
            createFileIfNotExist(TRANSACTIONS_FILE, "username,timestamp,symbol,type,quantity,price,totalAmount\n");
            createFileIfNotExist(WATCHLIST_FILE, "username,symbol\n");
        } catch (IOException e) {
            System.err.println("Failed to initialize database files: " + e.getMessage());
        }
    }

    private static void createFileIfNotExist(String filepath, String header) throws IOException {
        Path filePath = Paths.get(filepath);
        if (!Files.exists(filePath)) {
            Files.writeString(filePath, header);
        }
    }

    /**
     * Reads all users from users.csv.
     */
    public static synchronized List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line = br.readLine(); // Read header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    String username = tokens[0].trim();
                    String pwdHash = tokens[1].trim();
                    double balance = Double.parseDouble(tokens[2].trim());
                    users.add(new User(username, pwdHash, balance));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Saves all users to users.csv.
     */
    public static synchronized void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            bw.write("username,passwordHash,balance");
            bw.newLine();
            for (User user : users) {
                bw.write(user.getUsername() + "," + user.getPasswordHash() + "," + user.getBalance());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    /**
     * Appends or updates user details in users.csv.
     */
    public static synchronized void saveOrUpdateUser(User user) {
        List<User> users = loadUsers();
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equalsIgnoreCase(user.getUsername())) {
                users.set(i, user);
                found = true;
                break;
            }
        }
        if (!found) {
            users.add(user);
        }
        saveUsers(users);
    }

    /**
     * Loads portfolio items and maps them to their respective users.
     */
    public static synchronized void loadPortfolios(List<User> users) {
        // Create user lookup map
        Map<String, User> userMap = new HashMap<>();
        for (User u : users) {
            userMap.put(u.getUsername().toLowerCase(), u);
            u.getPortfolio().clear();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(PORTFOLIOS_FILE))) {
            String line = br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String username = tokens[0].trim();
                    String symbol = tokens[1].trim();
                    int quantity = Integer.parseInt(tokens[2].trim());
                    double avgPrice = Double.parseDouble(tokens[3].trim());

                    User user = userMap.get(username.toLowerCase());
                    if (user != null) {
                        user.getPortfolio().recordBuy(symbol, quantity, avgPrice);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading portfolios: " + e.getMessage());
        }
    }

    /**
     * Saves all portfolios from active users list.
     */
    public static synchronized void saveAllPortfolios(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PORTFOLIOS_FILE))) {
            bw.write("username,symbol,quantity,averageBuyPrice");
            bw.newLine();
            for (User user : users) {
                for (PortfolioItem item : user.getPortfolio().getHoldingsList()) {
                    bw.write(user.getUsername() + "," + item.getSymbol() + "," +
                            item.getQuantity() + "," + item.getAverageBuyPrice());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving portfolios: " + e.getMessage());
        }
    }

    /**
     * Appends a transaction record to transactions.csv.
     */
    public static synchronized void saveTransaction(Transaction tx) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            bw.write(tx.getUsername() + "," +
                    tx.getTimestamp() + "," +
                    tx.getSymbol() + "," +
                    tx.getType() + "," +
                    tx.getQuantity() + "," +
                    tx.getPrice() + "," +
                    tx.getTotalAmount());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }

    /**
     * Loads transaction history.
     * If username is null, loads all transactions; otherwise filters by user.
     */
    public static synchronized List<Transaction> loadTransactions(String targetUsername) {
        List<Transaction> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line = br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                if (tokens.length >= 7) {
                    String username = tokens[0].trim();
                    if (targetUsername == null || username.equalsIgnoreCase(targetUsername)) {
                        String timestamp = tokens[1].trim();
                        String symbol = tokens[2].trim();
                        String type = tokens[3].trim();
                        int quantity = Integer.parseInt(tokens[4].trim());
                        double price = Double.parseDouble(tokens[5].trim());
                        list.add(new Transaction(username, timestamp, symbol, type, quantity, price));
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        return list;
    }

    /**
     * Loads watchlists and assigns them to the users.
     */
    public static synchronized void loadWatchlists(List<User> users) {
        Map<String, User> userMap = new HashMap<>();
        for (User u : users) {
            userMap.put(u.getUsername().toLowerCase(), u);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(WATCHLIST_FILE))) {
            String line = br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    String username = tokens[0].trim();
                    String symbol = tokens[1].trim();

                    User user = userMap.get(username.toLowerCase());
                    if (user != null) {
                        user.addToWatchlist(symbol);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading watchlists: " + e.getMessage());
        }
    }

    /**
     * Saves all watchlists.
     */
    public static synchronized void saveAllWatchlists(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(WATCHLIST_FILE))) {
            bw.write("username,symbol");
            bw.newLine();
            for (User user : users) {
                for (String symbol : user.getWatchlist()) {
                    bw.write(user.getUsername() + "," + symbol);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving watchlists: " + e.getMessage());
        }
    }
}
