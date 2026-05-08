package com.example.luckycasino.view.Homescreen;

public class HomescreenPresenter {
    private HomescreenView view;
    public HomescreenPresenter(HomescreenView view) {
        this.view = view;
    }

    public void attemptLogin(String username) {

        if (username.isEmpty()) {
            view.onPlayerLogin(false, "Please enter a valid username", "");
            return;
        }

        boolean success = true;

        if (success) {
            view.onPlayerLogin(true, "Log in successfull", username);
        } else {
            view.showError("Login failed");
        }
    }
    public void onPlayer() {
        view.playerLogin(username);
    }

}
