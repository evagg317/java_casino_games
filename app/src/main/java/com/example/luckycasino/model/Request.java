package com.example.luckycasino.model;

public class Request {
    public static final String ADD_GAME = "ADD_GAME";
    public static final String REMOVE_GAME = "REMOVE_GAME";
    public static final String CHANGE_RISK = "CHANGE_RISK";
    public static final String FILTER_GAMES = "FILTER_GAMES";
    public static final String PLAY = "PLAY";
    public static final String GET_GAMES = "GET_GAMES";

    public static final String PLACE_BET = "PLACE_BET";
    public static final String RATE_GAME = "RATE_GAME";
    public static final String GET_PLAYER_STATS = "GET_PLAYER_STATS";
    public static final String GET_GAME_STATS = "GET_GAME_STATS";

    private String type;
    private String gameName;
    private String playerId;
    private String data;
    private double betAmount;
    private int rating;

    //search
    private int minStars;
    private String betCategory; // $, $$, $$$
    private String riskLevel;   // low, medium, high


    public Request(String type) {
        this.type = type;
    }

    //gia manager
    public Request(String type, String data) {
        this.type = type;
        if (type.equals(ADD_GAME))
            this.data = data;
        else
            this.gameName = data;
    }

    public Request(String type, String gameName, String riskLevel) {
        this.type = type;
        this.gameName = gameName;
        this.riskLevel = riskLevel;
    }

    //gia player
    public Request(String type, String filterType, Object value) {
        this.type = type;
        if (filterType.equals("stars")) this.minStars = (int) value;
        else if (filterType.equals("betting")) this.betCategory = (String) value;
        else if (filterType.equals("risk")) this.riskLevel = (String) value;
        System.out.println("Constructor: bc=" + betCategory + " rl=" + riskLevel);

    }

    public Request(String type, String playerId, String gameName, double betAmount) {
        this.type = type;
        this.playerId = playerId;
        this.gameName = gameName;
        this.betAmount = betAmount;
    }

    public Request(String type, String playerId, String gameName, int rating) {
        this.type = type;
        this.playerId = playerId;
        this.gameName = gameName;
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public String getGameName(){
        return gameName;
    }

    public String getPlayerId(){
        return playerId;
    }

    public String getData(){
        return data;
    }

    public double getBetAmount(){
        return betAmount;
    }

    public int getMinStars(){
        return minStars;
    }

    public String getBetCategory() {
        return betCategory;
    }

    public String getRiskLevel(){
        return riskLevel;
    }
    public int getRating() {
        return rating;
    }


    public void setGameName(String gameName){
        this.gameName = gameName;
    }
    public void setPlayerId(String playerId){
        this.playerId = playerId;
    }
    public void setData(String data){
        this.data = data;
    }
    public void setBetAmount(double betAmount){
        this.betAmount = betAmount;
    }
    public void setMinStars(int minStars){
        this.minStars = minStars;
    }
    public void setBetCategory(String betCategory){
        this.betCategory = betCategory;
    }
    public void setRiskLevel(String riskLevel){
        this.riskLevel = riskLevel;
    }


    public String serialize() {
        String g = "";
        String p = "";
        String d = "";
        String bc = "";
        String rl = "";

        if (gameName != null) g = gameName;
        if (playerId != null) p = playerId;
        if (data != null) d = data;
        if (betCategory != null) bc = betCategory;
        if (riskLevel != null) rl = riskLevel;

        return type + "|" + g + "|" + p + "|" + d + "|" + betAmount + "|" + minStars + "|" + bc + "|" + rl + "|" + rating;
    }

    public static Request deserialize(String raw) {

        String[] parts = raw.split("\\|", -1);

        Request r = new Request(parts[0]);

        if (parts.length > 1 && !parts[1].isEmpty())
            r.gameName = parts[1];
        if (parts.length > 2 && !parts[2].isEmpty())
            r.playerId = parts[2];
        if (parts.length > 3 && !parts[3].isEmpty())
            r.data = parts[3];
        if (parts.length > 4 && !parts[4].isEmpty())
            r.betAmount = Double.parseDouble(parts[4]);
        if (parts.length > 5 && !parts[5].isEmpty())
            r.minStars = Integer.parseInt(parts[5]);
        if (parts.length > 6 && !parts[6].isEmpty())
            r.betCategory = parts[6];
        if (parts.length > 7 && !parts[7].isEmpty())
            r.riskLevel = parts[7];
        if (parts.length > 8 && !parts[8].isEmpty()) {
            r.rating = Integer.parseInt(parts[8]);
        }

        return r;
    }
}
