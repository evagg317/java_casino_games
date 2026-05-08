package com.example.luckycasino.view.Homescreen;

public class HomescreenPresenter {
    private HomescreenView view;
    public HomescreenPresenter(HomescreenView view) {
        this.view = view;
    }
    public void onPlayer(String username) {
        if (username == null || username.trim().isEmpty()) {
            view.showError("Please enter your username");
            return;
        }
        view.playerLogin(username);
    }
}
