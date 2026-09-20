package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ManagerApp {

    public static void main(String[] args) {

        String masterHost = args.length > 0 ? args[0] : "localhost";
        int masterPort = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        System.out.println("Connecting to Master at " + masterHost + ":" + masterPort);

        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        while (running) {
            printMenu();
            int choice = Integer.parseInt(scanner.nextLine());

            try (Socket socket = new Socket(masterHost, masterPort);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream());   
                ){

                Request request = null;

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter JSON file path: ");
                        String path = scanner.nextLine();

                        try {
                            Game g = new Game(path);
                            request = new Request("ADD_GAME", path);
                            request.setGameName(g.getGameName());
                        } catch (Exception e) {
                            System.out.println("Error loading game file: " + e.getMessage());
                            continue;
                        }
                    }

                    case 2 -> {
                        System.out.print("Enter game name: ");
                        String gameName = scanner.nextLine();

                        request = new Request("REMOVE_GAME", gameName);
                    }

                    case 3 -> {
                        System.out.print("Enter game name: ");
                        String gameName = scanner.nextLine();

                        System.out.print("Enter new risk level (low/medium/high): ");
                        String risk = scanner.nextLine();

                        request = new Request(Request.CHANGE_RISK, gameName, risk);
                    }

                    case 4 -> {
                        System.out.print("Enter provider name: ");
                        String providerName = scanner.nextLine();
                        request = new Request(Request.GET_GAME_STATS);
                        request.setData(providerName);
                    }

                    case 5 -> {
                        System.out.print("Enter player name: ");
                        String playerId = scanner.nextLine();
                        request = new Request(Request.GET_PLAYER_STATS);
                        request.setPlayerId(playerId);
                    }

                    case 6 -> {
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
            }
            catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            }
        } 
        scanner.close();
    }
    

    private static void printMenu() {
        System.out.println("\n Manager Menu ");
        System.out.println("1. Add Game");
        System.out.println("2. Remove Game");
        System.out.println("3. Update Risk Level");
        System.out.println("4. Show Provider Stats");
        System.out.println("5. Show Player Stats");
        System.out.println("6. Exit");
        System.out.print("Choose option: ");
    }
}