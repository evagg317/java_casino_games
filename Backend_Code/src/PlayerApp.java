package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class PlayerApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String masterHost = args.length > 0 ? args[0] : "localhost";
        int masterPort = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        System.out.println("Connected to Master.");

        System.out.print("Enter your player name: ");
        String playerName = scanner.nextLine();

        boolean running = true;

        while (running) {
            printMenu();
            try (
                Socket socket = new Socket(masterHost, masterPort);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());)
                {

                int choice = Integer.parseInt(scanner.nextLine());

                Request request = null;

                switch (choice) {

                    case 1 -> request = new Request("GET_GAMES");

                    case 2 -> {
                        System.out.println("Filter by:");
                        System.out.println("1. Stars");
                        System.out.println("2. Betting Category");
                        System.out.println("3. Risk Level");

                        int filterChoice = Integer.parseInt(scanner.nextLine());

                        switch (filterChoice) {

                            case 1 -> {
                                System.out.print("Enter minimum stars (1-5): ");
                                int stars = Integer.parseInt(scanner.nextLine());

                                request = new Request("FILTER_GAMES", "stars", stars);
                            }

                            case 2 -> {
                                System.out.print("Enter betting category ($ / $$ / $$$): ");
                                String category = scanner.nextLine();

                                request = new Request("FILTER_GAMES");
                                request.setBetCategory(category);
                            }

                            case 3 -> {
                                System.out.print("Enter risk level (low/medium/high): ");
                                String risk = scanner.nextLine();

                                request = new Request("FILTER_GAMES", "risk", risk);
                            
                            }

                            default -> {
                                System.out.println("Invalid filter option.");
                                continue;
                            }
                        }
        
                    }

                    case 3 -> {
                        System.out.print("Enter game name: ");
                        String gameName = scanner.nextLine();

                        System.out.print("Enter bet amount: ");
                        double amount = Double.parseDouble(scanner.nextLine());

                        request = new Request(Request.PLAY, playerName, gameName, amount);
                    }

                    case 4 -> {
                        System.out.print("Enter game name: ");
                        String gameName = scanner.nextLine();

                        System.out.print("Enter rating (1-5): ");
                        int rating = Integer.parseInt(scanner.nextLine());

                        request = new Request("RATE_GAME", playerName, gameName, rating);
                    }

                    case 5 -> {
                        System.out.println("Exiting...");
                        running = false;
                        continue;
                    }

                    default -> {
                        System.out.println("Invalid option.");
                        continue;
                    }
                }
                out.writeUTF(request.serialize());
                out.flush();

                String rawResponse = in.readUTF(); 
                Response response = Response.deserialize(rawResponse);

                System.out.println("\n--- Response ---");
                System.out.println(response.getMessage());
                if (response.getData() != null && !response.getData().trim().isEmpty()) {
                    System.out.println("\n--- Results ---");
                    String[] items = response.getData().split(";");
                    for (String item : items) {
                        if (!item.trim().isEmpty()) {
                            System.out.println("- " + item.replace(",", " | "));
                        }
                    }
                }
                System.out.println("----------------\n");
            }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            }

        }
        scanner.close();
    }


    private static void printMenu() {
        System.out.println("\n=== Player Menu ===");
        System.out.println("1. Show All Games");
        System.out.println("2. Filter Games");
        System.out.println("3. Place Bet");
        System.out.println("4. Rate Game");
        System.out.println("5. Exit");
        System.out.print("Choose option: ");
    }
}