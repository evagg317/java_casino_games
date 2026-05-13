package com.example.luckycasino.view.ShowAllGames;

import java.util.List;

public interface ShowAllGamesView {

    /**
     * Εμφανίζει ένα μήνυμα λάθους στον χρήστη.
     * @param message Το μήνυμα προς εμφάνιση
     */
    void showError(String message);

    /**
     * Ορίζει τη λίστα παιχνιδιών για εμφάνιση.
     * @param games Η λίστα με τα raw strings των παιχνιδιών
     */
    void setGameList(List<String> games);
}