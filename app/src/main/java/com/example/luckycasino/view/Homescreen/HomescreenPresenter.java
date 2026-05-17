package com.example.luckycasino.view.Homescreen;

public class HomescreenPresenter {
    private HomescreenView view;
    public HomescreenPresenter(HomescreenView view) {
        this.view = view;
    }
    public void onPlayer(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            view.showError("Please enter your username and password");
            return;
        }
        view.playerLogin(username, password);
    }
}
