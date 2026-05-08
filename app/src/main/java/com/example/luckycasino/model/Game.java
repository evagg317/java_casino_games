package com.example.luckycasino.model;
import java.io.*;

public class Game {

    // apo JSON
    private String gameName;
    private String providerName;
    private int stars;
    private int noOfVotes;
    private String gameLogo;
    private double minBet;
    private double maxBet;
    private String riskLevel;
    private String hashKey;

    private String betCategory;
    private int jackpot;

    private static final double[] LOW = {0.0, 0.0, 0.0, 0.1, 0.5, 1.0, 1.1, 1.3, 2.0, 2.5};
    private static final double[] MEDIUM = {0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 1.0, 1.5, 2.5, 3.5};
    private static final double[] HIGH = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 6.5};

    private double totalProfitLoss = 0.0;
    private boolean active = true;

    public Game(String jsonFilePath) throws Exception {

        readJson(jsonFilePath);
        this.betCategory = calculateBetCategory(this.minBet);
        this.jackpot = calculateJackpot(this.riskLevel);
    }

    private void parseJson(String json) {
        this.gameName = extractString(json, "GameName");
        this.providerName = extractString(json, "ProviderName");
        this.stars = (int) extractNumber(json, "Stars");
        this.noOfVotes = (int) extractNumber(json, "NoOfVotes");
        this.gameLogo = extractString(json, "GameLogo");
        this.minBet = extractNumber(json, "MinBet");
        this.maxBet = extractNumber(json, "MaxBet");
        this.riskLevel = extractString(json, "RiskLevel");
        this.hashKey = extractString(json, "HashKey");
    }

    private String extractString(String json, String key){
        String search = "\"" + key + "\"";
        int index = json.indexOf(search);

        if (index == -1) return "";
        int colon = json.indexOf(":", index);
        int start = json.indexOf("\"", colon + 1);
        int end = json.indexOf("\"", start + 1);
        return json.substring(start + 1, end);
    }

    private double extractNumber(String json,String key){
        String search = "\"" + key + "\"";
        int index = json.indexOf(search);
        if (index == -1) return 0;
        int colon = json.indexOf(":", index);
        int start = colon + 1;
        while (start < json.length() && json.charAt(start) == ' ')
            start ++;
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.'))
            end ++;
        return Double.parseDouble(json.substring(start, end));
    }

    private void readJson(String jsonFilePath) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        parseJson(sb.toString());
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
        this.jackpot = calculateJackpot(riskLevel);
    }

    private int calculateJackpot(String riskLevel){
        if ( riskLevel.toLowerCase().equals("high"))
            return 40;
        else if ( riskLevel.toLowerCase().equals("medium"))
            return 20;
        else
            return 10;
    }

    private String calculateBetCategory (double minBet){
        if (minBet >= 5.0)
            return "$$$";
        if (minBet >= 1)
            return "$$";
        return "$";

    }

    public double getMultiplier(int index){
        if ( riskLevel.toLowerCase().equals("high"))
            return HIGH[index];
        if ( riskLevel.toLowerCase().equals("medium"))
            return MEDIUM[index];
        return LOW[index];
    }

    public synchronized void updateProfitLoss(double amount) {
        this.totalProfitLoss += amount;
    }

    public String getGameName() {
        return gameName;
    }
    public String getProviderName() {
        return providerName;
    }
    public int getStars() {
        return stars;
    }
    public int getNoOfVotes() {
        return noOfVotes;
    }
    public String getGameLogo() {
        return gameLogo;
    }
    public double getMinBet() {
        return minBet;
    }
    public double getMaxBet() {
        return maxBet;
    }
    public String getRiskLevel() {
        return riskLevel;
    }
    public String getHashKey() {
        return hashKey;
    }
    public String getBetCategory() {
        return betCategory;
    }
    public int getJackpot() {
        return jackpot;
    }
    public double getTotalProfitLoss() {
        return totalProfitLoss;
    }
    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }

    public synchronized void addRating(int newRating) {
        int totalStars = this.stars * this.noOfVotes;

        this.noOfVotes++;

        double average = (double) (totalStars + newRating) / this.noOfVotes;
        this.stars = (int) Math.round(average);
    }
}