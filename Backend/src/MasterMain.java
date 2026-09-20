package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MasterMain {

    public static void main(String[] args) {
        if (args.length < 4) {
            return;
        }

        int masterPort = Integer.parseInt(args[0]);
        int numberOfWorkers = Integer.parseInt(args[1]);
        String reducerHost = args[2];
        int reducerPort = Integer.parseInt(args[3]);

        String[] workerHosts = new String[numberOfWorkers];
        int[] workerPorts = new int[numberOfWorkers];

        for (int i = 0; i < numberOfWorkers; i++) {
            workerHosts[i] = args[4 + i * 2];
            workerPorts[i] = Integer.parseInt(args[5 + i * 2]);
        }

        System.out.println("Master started at port " + masterPort);
        System.out.println("Managing " + numberOfWorkers + " workers");
        System.out.println("Reducer at " + reducerHost + ":" + reducerPort);
        for (int i = 0; i < numberOfWorkers; i++) {
            System.out.println("Worker " + i + " at " + workerHosts[i] + ":" + workerPorts[i]);
        }
        

        try (ServerSocket serverSocket = new ServerSocket(masterPort);)
        {
            System.out.println("Master started at port " + masterPort);
            System.out.println("Managing " + numberOfWorkers + " workers:");
            System.out.println("Reducer -> " + reducerHost + ":" + reducerPort);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("New connection from: " + client.getInetAddress());

                new HandleThreadMaster(client, workerHosts, workerPorts, numberOfWorkers, reducerHost, reducerPort).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}