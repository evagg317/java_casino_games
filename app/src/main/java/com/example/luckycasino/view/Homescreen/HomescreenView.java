package com.example.luckycasino.view.Homescreen;
public interface HomescreenView {
    void showUserDetails(String name);
    void onPlayerLogin(boolean success, String message, String username);
    void showError(String message);
}