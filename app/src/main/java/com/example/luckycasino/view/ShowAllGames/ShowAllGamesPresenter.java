package com.example.luckycasino.view.ShowAllGames;
import com.example.luckycasino.model.Request;
import com.example.luckycasino.model.Response;
import com.example.luckycasino.model.TCP;

import java.util.ArrayList;
import java.util.List;

/**
 * Ο GamesLobbyPresenter διαχειρίζεται τη λογική για την GamesLobbyView.
 * Επικοινωνεί με τον Master μέσω TCP για φόρτωση και φιλτράρισμα παιχνιδιών.
 */
public class ShowAllGamesPresenter {

        private ShowAllGamesView view;

        /**
         * Κατασκευαστής του GamesLobbyPresenter.
         * @param view Το view που θα ενημερώνεται από τον presenter
         */
        public ShowAllGamesPresenter(ShowAllGamesView view) {
            this.view = view;
        }

        /**
         * Φορτώνει όλα τα παιχνίδια από τον Master (GET_GAMES).
         * Η επικοινωνία γίνεται ασύγχρονα σε background thread.
         */
        public void loadAllGames() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Request req = new Request(Request.GET_GAMES);
                        TCP tcp = new TCP();
                        String rawResponse = tcp.sendRequest(req.serialize());
                        Response resp = Response.deserialize(rawResponse);

                        if (resp.isSuccess() && resp.getData() != null) {
                            List<String> games = parseGames(resp.getData());
                            view.setGameList(games);
                        } else {
                            view.showError("No games found");
                        }
                    } catch (Exception e) {
                        view.showError("Connection error: " + e.getMessage());
                    }
                }
            }).start();
        }

        /**
         * Φιλτράρει παιχνίδια βάσει κριτηρίων (FILTER_GAMES).
         * Η επικοινωνία γίνεται ασύγχρονα σε background thread.
         * @param minStars    ελάχιστος αριθμός αστεριών (0 = χωρίς φίλτρο)
         * @param betCategory κατηγορία πονταρίσματος ($, $$, $$$) ή "" για όλες
         * @param riskLevel   επίπεδο ρίσκου (low, medium, high) ή "" για όλα
         */
        public void filterGames(int minStars, String betCategory, String riskLevel) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Request req = new Request(Request.FILTER_GAMES);
                        req.setMinStars(minStars);
                        if (!betCategory.isEmpty()) req.setBetCategory(betCategory);
                        if (!riskLevel.isEmpty())   req.setRiskLevel(riskLevel);

                        TCP tcp = new TCP();
                        String rawResponse = tcp.sendRequest(req.serialize());
                        Response resp = Response.deserialize(rawResponse);

                        if (resp.isSuccess() && resp.getData() != null) {
                            List<String> games = parseGames(resp.getData());
                            view.setGameList(games);
                        } else {
                            view.showError("No games found");
                        }
                    } catch (Exception e) {
                        view.showError("Connection error: " + e.getMessage());
                    }
                }
            }).start();
        }

        /**
         * Μετατρέπει το raw string από τον Master σε λίστα strings.
         * Format εισόδου: "GameName,Provider,Stars,MinBet,MaxBet,RiskLevel,BetCategory,Jackpot;"
         * @param data το raw string από τον Master
         * @return λίστα με τα raw strings κάθε παιχνιδιού
         */
        public List<String> parseGames(String data) {
            List<String> result = new ArrayList<>();
            if (data == null || data.isEmpty()) return result;

            String[] entries = data.split(";");
            for (String entry : entries) {
                if (!entry.trim().isEmpty()) {
                    result.add(entry.trim());
                }
            }
            return result;
        }

    /**
     * Στέλνει τη βαθμολογία του παίκτη στον Server.
     */
    public void rateGame(String customerId, String gameName, int stars) {
        new Thread(() -> {
            try {
                // Χρησιμοποιούμε τον Constructor που έχεις ήδη φτιάξει!
                Request req = new Request(Request.RATE_GAME, customerId, gameName, stars);

                TCP tcp = new TCP();
                String rawResponse = tcp.sendRequest(req.serialize());
                Response resp = Response.deserialize(rawResponse);

                if (resp.isSuccess()) {
                    view.showError("Ευχαριστούμε! Η βαθμολογία (" + stars + "★) αποθηκεύτηκε.");
                } else {
                    view.showError("Αποτυχία: " + resp.getMessage());
                }
            } catch (Exception e) {
                view.showError("Σφάλμα σύνδεσης: " + e.getMessage());
            }
        }).start();
    }
}



