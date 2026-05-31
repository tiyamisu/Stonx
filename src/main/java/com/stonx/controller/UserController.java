package com.stonx.controller;

import com.stonx.model.User;
import com.stonx.service.UserService;

import java.util.List;

/**
 * Controller part of MVC architecture.
 * Manages user logins, signups, leaderboard retrieval, and watchlist actions.
 */
public class UserController {
    private final UserService userService;

    public UserController() {
        this.userService = UserService.getInstance();
    }

    public boolean login(String username, String password) {
        return userService.login(username, password);
    }

    public boolean register(String username, String password) {
        return userService.register(username, password);
    }

    public void logout() {
        userService.logout();
    }

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    public void toggleWatchlist(String symbol) {
        userService.toggleWatchlist(symbol);
    }

    public boolean isWatching(String symbol) {
        User user = userService.getCurrentUser();
        return user != null && user.isWatching(symbol);
    }

    public List<UserService.LeaderboardEntry> getLeaderboard() {
        return userService.getLeaderboard();
    }
    
    public void saveUserData() {
        userService.saveCurrentUserData();
    }
}
