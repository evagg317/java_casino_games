package com.example.luckycasino.view.PlaceBet;

public interface PlaceBetView {
    void displayGameInfo(String name, int stars, String logoResName);
    void showResult(String message);
    void showError(String message);
}
