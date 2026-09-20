package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkerMain {

	public static void main(String[] args) {

		if (args.length < 6) {
            return;
        }

		int workerId = Integer.parseInt(args[0]);
		int port = Integer.parseInt(args[1]);
		int numberOfWorkers = Integer.parseInt(args[2]);
		String gamesFolder = args[3];
		String srgHost = args[4];
		int srgPort = Integer.parseInt(args[5]);

		try (ServerSocket serverSocket = new ServerSocket(port);)
		{			
			System.out.println("Worker " + workerId + " started at port " + port);
				
			// Auto-load JSONs 
			loadGamesFromFolder(gamesFolder, workerId, numberOfWorkers);

			while(true) {
				Socket client = serverSocket.accept();
				new HandleThreadWorker(client,workerId, srgHost, srgPort).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void loadGamesFromFolder(String folderPath, int workerId, int numberOfWorkers) {
		java.io.File folder = new java.io.File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			System.out.println("Worker " + workerId + ": No games folder found at " + folderPath);
			return;
		}
	
		for (java.io.File file : folder.listFiles()) {
			if (file.getName().endsWith(".json")) {
				try {
					Game g = new Game(file.getAbsolutePath());
					int targetWorker = Math.abs(g.getGameName().hashCode()) % numberOfWorkers;

                if (targetWorker == workerId) {
                    HandleThreadWorker.addGameStatic(g);
                    System.out.println("Worker " + workerId + ": Loaded " + g.getGameName());
                }
				} catch (Exception e) {
					System.out.println("Worker " + workerId + ": Failed to load " + file.getName());
				}
			}
		}
	}

}
