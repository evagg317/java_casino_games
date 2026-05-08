package com.example.luckycasino.view.Homescreen;

public class HomescreenPresenter {
    private HomescreenView view;
    public HomescreenPresenter(HomescreenView view) {
        this.view = view;
    }
    public void onManager() {
        view.managerLogin();
    }
    public void onPlayer() {
        view.playerLogin(String username);
    }

}
