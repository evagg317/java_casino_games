package src;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HandleThreadReducer extends Thread {

    private Socket client;

    public HandleThreadReducer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            DataInputStream  input  = new DataInputStream(client.getInputStream());
            DataOutputStream output = new DataOutputStream(client.getOutputStream());

            // Receive data apo Master
            String raw = input.readUTF();
            System.out.println("Reducer received request: " + (raw.length() > 30 ? raw.substring(0, 30) + "..." : raw));

            // Split Type and Data
            String[] parts = raw.split("\\|", 2);
            String type = parts[0];
            String data = parts.length > 1 ? parts[1] : "";

            String result;

            // Choose the correct reduce method based on Request type
            if (type.equals(Request.GET_PLAYER_STATS)) {
                result = reducePlayerStats(data);
            } else if (type.equals(Request.GET_GAME_STATS)) {
                result = reduceGameStats(data);
            } else {
                result = reduceSearch(data);
            }

            // Send results back to Master
            output.writeUTF(result);
            output.flush();

            client.close();

        } catch (IOException e) {
            System.err.println("Reducer Thread Error: " + e.getMessage());
        }
    }

    private String reduceSearch(String allData) {
        if (allData == null || allData.isEmpty()) return "";

        String[] games = allData.split(";");
        List<String> seenGames = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        for (String game : games) {
            if (game.trim().isEmpty()) continue;
            
            String gameName = game.split(",")[0];
            
            if (!seenGames.contains(gameName)) {
                seenGames.add(gameName);
                result.append(game).append(";");
            }
        }

        System.out.println("Reducer: filtered to " + seenGames.size() + " unique games.");
        return result.toString();
    }

    private String reducePlayerStats(String allData) {
        if (allData == null || allData.isEmpty()) return "";

        HashMap<String, Double> totals = new HashMap<>();
        String[] entries = allData.split(";");

        for (String entry : entries) {
            if (entry.trim().isEmpty()) continue;
            String[] parts = entry.split(",");
            if (parts.length < 2) continue;

            String playerId = parts[0];
            try {
                double amount = Double.parseDouble(parts[1]);
                totals.put(playerId, totals.getOrDefault(playerId, 0.0) + amount);
            } catch (NumberFormatException e) {
                System.err.println("Reducer: Error parsing amount for player " + playerId);
            }
        }

        StringBuilder result = new StringBuilder();
        for (String id : totals.keySet()) {
            result.append(id).append(",").append(totals.get(id)).append(";");
        }
        
        System.out.println("Reducer: aggregated stats for " + totals.size() + " players.");
        return result.toString();
    }

    private String reduceGameStats(String allData) {
        if (allData == null || allData.isEmpty()) return "";

        HashMap<String, Double> gameTotals = new HashMap<>();
        double grandTotal = 0.0;
        String[] entries = allData.split(";");

        for (String entry : entries) {
            if (entry.trim().isEmpty()) continue;
            String[] parts = entry.split(",");
            if (parts.length < 3) continue;

            String gameName = parts[1]; 
            try {
                double amount = Double.parseDouble(parts[2]);
                gameTotals.put(gameName, gameTotals.getOrDefault(gameName, 0.0) + amount);
                grandTotal += amount;
            } catch (NumberFormatException e) {
                System.err.println("Reducer: Error parsing amount for game " + gameName);
            }
        }

        StringBuilder result = new StringBuilder();
        for (String name : gameTotals.keySet()) {
            result.append(name).append(",").append(gameTotals.get(name)).append(";");
        }
        result.append("Total,").append(grandTotal).append(";");

        System.out.println("Reducer: aggregated stats for " + gameTotals.size() + " games.");
        return result.toString();
    }
}