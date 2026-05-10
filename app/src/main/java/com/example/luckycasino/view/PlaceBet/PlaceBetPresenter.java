package com.example.luckycasino.view.PlaceBet;

import com.example.luckycasino.model.Request;
import com.example.luckycasino.model.Response;
import com.example.luckycasino.model.TCP;

public class PlaceBetPresenter {

    private PlaceBetView view;

    public PlaceBetPresenter(PlaceBetView view) {
        this.view = view;
    }

    public void loadData(String gameName, int stars, String logo) {
        if (gameName != null) {
            view.displayGameInfo(gameName, stars, logo);
        } else {
            view.showError("Error loading game data.");
        }
    }

    public void onPlaceBetClicked(String playerId, String gameName, String betAmountStr) {
        if (betAmountStr == null || betAmountStr.trim().isEmpty()) {
            view.showError("Please enter a bet amount");
            return;
        }

        try {
            double betAmount = Double.parseDouble(betAmountStr);

            // trexo diktio se ksexoristo thread
            new Thread(() -> {
                try {
                    Request req = new Request(Request.PLAY, playerId, gameName, betAmount);

                    // stelno meso tcp
                    TCP tcpClient = new TCP();
                    String rawResponse = tcpClient.sendRequest(req.serialize());

                    Response resp = Response.deserialize(rawResponse);

                    // stelno apotelesma piso sto View
                    if (resp.isSuccess()) {
                        view.showResult(resp.getMessage());
                    } else {
                        view.showError(resp.getMessage());
                    }

                } catch (Exception e) {
                    view.showError("Network Error: " + e.getMessage());
                }
            }).start();

        } catch (NumberFormatException e) {
            view.showError("Invalid bet amount format");
        }
    }
}