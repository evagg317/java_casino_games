package src;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HandleThreadWorker extends Thread {

    private Socket client;
    private int workerId;
    private String srgHost;
    private int srgPort;

    private static HashMap<String, Game> games = new HashMap<>();
    private static HashMap<String, Double> playerStats = new HashMap<>();


    public HandleThreadWorker(Socket client, int workerId, String srgHost, int srgPort) {
        this.client   = client;
        this.workerId = workerId;
        this.srgHost = srgHost;
        this.srgPort = srgPort;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(client.getInputStream());
            DataOutputStream output = new DataOutputStream(client.getOutputStream());

            String raw = input.readUTF();
            System.out.println("Worker " + workerId + " received: " + raw);

            String result = handleRequest(raw);

            output.writeUTF(result);
            output.flush();

            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleRequest(String raw) {
        Request req = Request.deserialize(raw);

        switch (req.getType()) {
            case Request.ADD_GAME: return addGame(req.getData());
            case Request.REMOVE_GAME: return removeGame(req.getGameName());
            case Request.CHANGE_RISK: return changeRiskLevel(req.getGameName(), req.getRiskLevel());
            case Request.FILTER_GAMES: return filterGames(req.getMinStars(), req.getBetCategory(), req.getRiskLevel());
            case Request.PLAY: return placeBet(req.getPlayerId(), req.getGameName(), req.getBetAmount());
            case Request.GET_GAMES: return getAllGames();
            case Request.RATE_GAME: return rateGame(req.getGameName(), req.getRating());
            case Request.GET_GAME_STATS: return getGameStats(req.getData());
            case Request.GET_PLAYER_STATS: return getPlayerStats(req.getPlayerId());
            default: return new Response(false, null, "Unknown request").serialize();
        }
    }

    private String addGame(String jsonPath) {
        try {
            Game g = new Game(jsonPath);
            
            synchronized (games) {
                // elegxos an yparxei to paixnidi
                if (games.containsKey(g.getGameName())) {
                    Game existingGame = games.get(g.getGameName());
                    
                    if (existingGame.isActive()) {
                        // nai
                        return new Response(false, null, "Game '" + g.getGameName() + "' already exists and is active!").serialize();
                    } else {
                        // nai alla einai anenergo - energopoihsh
                        existingGame.setActive(true);
                        System.out.println("Worker " + workerId + ": Re-activated " + g.getGameName());
                        return new Response(true, null, "Game '" + g.getGameName() + "' was inactive and has been restored successfully!").serialize();
                    }
                }
                
                // oxi, to vazoyme
                games.put(g.getGameName(), g);
            }
            
            System.out.println("Worker " + workerId + ": Added " + g.getGameName());
            return new Response(true, null, "Game added: " + g.getGameName()).serialize();
            
        } catch (Exception e) {
            return new Response(false, null, "Failed: " + e.getMessage()).serialize();
        }
    }

    private String removeGame(String gameName) {
        synchronized (games) {
            Game g = games.get(gameName);
            
            if (g != null) {
                g.setActive(false); // Soft Delete
                System.out.println("Worker " + workerId + ": Game " + gameName + " set to inactive.");
                return new Response(true, "", "Game " + gameName + " removed successfully").serialize();
            } else {
                return new Response(false, "", "Game not found in memory").serialize();
            }
        }
    }

    private String getAllGames() {
        List<Game> result = new ArrayList<>();

        synchronized (games) {
            for (Game g : games.values()) {

                if (g.isActive()) {
                    result.add(g);
                }
            }
        }

        return new Response(true, serializeGameList(result), result.size() + " games found").serialize();
    }

    private String rateGame(String gameName, int rating) {
        synchronized (games) {
            Game game = games.get(gameName);
            
            if (game == null || !game.isActive()) {
                return new Response(false, null, "Game '" + gameName + "' not found or inactive.").serialize();
            }
            
            if (rating < 1 || rating > 5) {
                return new Response(false, null, "Rating must be between 1 and 5.").serialize();
            }

            game.addRating(rating);
            
            return new Response(true, null, "Successfully rated " + gameName + " with " + rating + " stars!").serialize();
        }
    }

    private String getGameStats(String providerName) {
        StringBuilder sb = new StringBuilder();
     
        synchronized (games) {
            for (Game g : games.values()) {
                if (g.getProviderName().equals(providerName)) {
                    sb.append(g.getProviderName()).append(",").append(g.getGameName()).append(",").append(g.getTotalProfitLoss()).append(";");
                }
            }
        }
     
        // o Reducer kanei tin prosthesi
        return new Response(true, sb.toString(), null).serialize();
    }

    private String getPlayerStats(String playerId) {
        StringBuilder sb = new StringBuilder();
        
        synchronized (playerStats) {
            if (playerId != null && !playerId.isEmpty()) {
                // gia sygkekrimeno paikti (PlayerApp)
                double stat = playerStats.getOrDefault(playerId, 0.0);
                sb.append(playerId).append(",").append(stat).append(";");
            } else {
                // gia olous tous paiktes (ManagerApp)
                for (String pId : playerStats.keySet()) {
                    sb.append(pId).append(",").append(playerStats.get(pId)).append(";");
                }
            }
        }
        
        return new Response(true, sb.toString(), "Player stats retrieved successfully.").serialize();
    }

    private String changeRiskLevel(String gameName, String newRiskLevel) {
        synchronized (games) {
            Game g = games.get(gameName); // psaxnei sto hashmap
            if (g != null) g.setRiskLevel(newRiskLevel);
        }
        return new Response(true, null, "Risk level updated").serialize();
    }

    private String filterGames(int minStars, String betCategory, String riskLevel) {
        List<Game> result = new ArrayList<>();
        System.out.println("Filter - minStars:" + minStars + " betCategory:" + betCategory + " riskLevel:" + riskLevel);
        
        synchronized (games) {
            for (Game g : games.values()) {
                if (!g.isActive()) continue; 
                if (minStars > 0 && g.getStars() < minStars) continue;
                if (betCategory != null && !betCategory.trim().isEmpty() && !g.getBetCategory().trim().equals(betCategory.trim())) continue;
                if (riskLevel   != null && !riskLevel.isEmpty()   && !g.getRiskLevel().equalsIgnoreCase(riskLevel)) continue;
                System.out.println("DEBUG: Comparing '" + g.getBetCategory() + "' with '" + betCategory + "'");
                result.add(g);
            }
        }

        return new Response(true, serializeGameList(result), result.size() + " games found").serialize();
    }


    private String placeBet(String playerId, String gameName, double betAmount) {
        String error = validateBet(gameName, betAmount);
        if (error != null) 
            return error;

        Game game;
        synchronized (games) {
            game = games.get(gameName);
        }

        // pairnei tyxaio arithmo apo SRG
        int randomNumber = getFromSRG(game.getHashKey());
        if (randomNumber < 0) {
            return new Response(false, null, "Could not get random number").serialize();
        }

        double winnings;
        String resultMsg;

        if (randomNumber % 100 == 0) {
            winnings = betAmount * game.getJackpot();
            resultMsg = "JACKPOT! You won: " + String.format(java.util.Locale.US, "%.2f", winnings);
        } else {
            int i = randomNumber % 10;
            double multiplier = game.getMultiplier(i);
            winnings = betAmount * multiplier;
            resultMsg = "Result: " + String.format(java.util.Locale.US, "%.2f", winnings);
        }

        double playerProfit = winnings - betAmount;
        double gameProfit = -playerProfit;
        game.updateProfitLoss(gameProfit);
        updateStatistics(playerId, playerProfit);

        return new Response(true, null, resultMsg).serialize();
    }

    private static synchronized void updateStatistics(String playerId, double amount) {
        double current = 0.0;
        if (playerStats.containsKey(playerId)) {
            current = playerStats.get(playerId);
        }
        playerStats.put(playerId, current + amount);
    }

    private String validateBet(String gameName, double betAmount) {
        synchronized (games) {
            Game game = games.get(gameName);
            if (game == null || !game.isActive()) {
                return new Response(false, null, "Game not found or inactive").serialize();
            }
            if (betAmount < game.getMinBet() || betAmount > game.getMaxBet()) {
                return new Response(false, null, "Bet out of range").serialize();
            }
        }
        return null;
    }

    private String serializeGameList(List<Game> gameList) {
        StringBuilder sb = new StringBuilder();
        for (Game g : gameList) {
            sb.append(g.getGameName()).append(",")
              .append(g.getProviderName()).append(",")
              .append(g.getStars()).append(",")
              .append(g.getMinBet()).append(",")
              .append(g.getMaxBet()).append(",")
              .append(g.getRiskLevel()).append(",")
              .append(g.getBetCategory()).append(",")
              .append(g.getJackpot()).append(";");
        }
        return sb.toString();
    }

    private int getFromSRG(String secret) {

        try {
            Socket srgSocket = new Socket(srgHost, srgPort);

        // stelnei prota hashKey gia ton SRG
            DataOutputStream out = new DataOutputStream(srgSocket.getOutputStream());
            out.writeUTF(secret);
            out.flush();

        // diabazei ton arithmo + hash poy esteile o SRG
            BufferedReader in = new BufferedReader(new InputStreamReader(srgSocket.getInputStream()));
            String response = in.readLine();

            srgSocket.close();

            if (response == null) {
                System.err.println("Worker " + workerId + ": SRG returned null");
                return -1;
            }

            String[] parts = response.split(" ");
            int number  = Integer.parseInt(parts[0]);
            String hash = parts[1];

            String expected = SHA.hash(number + secret);
            if (!expected.equals(hash)) {
                System.err.println("Worker " + workerId + ": Hash mismatch!");
                return -1;
            }

            return number;

        } catch (Exception e) {
            System.err.println("Worker " + workerId + ": SRG error: " + e.getMessage());
            return -1;
        }
    }


    public static void addGameStatic(Game g) {
        synchronized (games) {
            games.put(g.getGameName(), g);
        }
    }
}