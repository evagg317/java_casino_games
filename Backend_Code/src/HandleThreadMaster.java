package src;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class HandleThreadMaster extends Thread {
    private Socket client;
    private int numberOfWorkers ;
    private String[] workerHosts;
    private int[] workerPorts;
    private String reducerHost;
    private int reducerPort;


    public HandleThreadMaster(Socket client,String[] workerHosts, int[] workerPorts, int numberOfWorkers, String reducerHost, int reducerPort){
        this.client = client;
        this.numberOfWorkers = numberOfWorkers;
        this.workerHosts = workerHosts;
        this.workerPorts = workerPorts;
        this.reducerHost = reducerHost;
        this.reducerPort = reducerPort;
    }

    @Override
    public void run() {
        System.out.println("---------------------------------");
        System.out.println("New request accepted");
 
        try {
            DataOutputStream clientOutput = new DataOutputStream(client.getOutputStream());
            DataInputStream  clientInput  = new DataInputStream(client.getInputStream());
            
            // diabase to request apo Manager h Player
            String raw = clientInput.readUTF();
            System.out.println("Master received: " + raw);
 
            // epeksergasia request
            String result = handleRequest(raw);

            // stelnei to apotelesma piso
            clientOutput.writeUTF(result);
            clientOutput.flush();
 
            client.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String handleRequest(String raw){
        
        Request req = Request.deserialize(raw);
        String workerResult; 
 
        if (req.getType().equals(Request.FILTER_GAMES) || req.getType().equals(Request.GET_GAMES) || req.getType().equals(Request.GET_GAME_STATS) || req.getType().equals(Request.GET_PLAYER_STATS)) {
            workerResult = handleSearch(raw);
        } else {
            workerResult = sendToWorker(raw, req);
        }

        if (workerResult.startsWith("1|") || workerResult.startsWith("0|")) {
            return workerResult;
        } else {
            return new Response(true, workerResult, "").serialize();
        }
    }

    private String sendToWorker(String data, Request req) {

        String key = req.getGameName() != null ? req.getGameName() : req.getData();
        int workerIndex = Math.abs(key.hashCode()) % numberOfWorkers;
        int workerPort = workerPorts[workerIndex];
        System.out.println("Master: Sending " + req.getType() + " for " + key + " to Worker index " + workerIndex + " at port " + workerPort);
        try {
            Socket workerSocket = new Socket(workerHosts[workerIndex], workerPorts[workerIndex]);
 
            DataOutputStream workerOut = new DataOutputStream(workerSocket.getOutputStream());
            workerOut.writeUTF(data);
            workerOut.flush();

            DataInputStream workerIn = new DataInputStream(workerSocket.getInputStream());
            String result = workerIn.readUTF();
 
            workerSocket.close();
 
            System.out.println("Result from Worker " + workerIndex + ": " + result);
            return result;
 
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(false, null, "Worker unavailable").serialize();
        }
    }
 

   private String handleSearch(String raw) {
        StringBuilder allResults = new StringBuilder();
 
        // MAP phase
        for (int i = 0; i < numberOfWorkers; i++) {
            try {
                //opens TCP
                Socket workerSocket = new Socket(workerHosts[i], workerPorts[i]);
                
                // stelnei se kathe worker to aitima 
                DataOutputStream workerOut = new DataOutputStream(workerSocket.getOutputStream());
                workerOut.writeUTF(raw);
                workerOut.flush();

                //diavazei to aitima 
                DataInputStream workerIn = new DataInputStream(workerSocket.getInputStream());
                String result = workerIn.readUTF();
                
                // kleinei th syndesh TCP
                workerSocket.close();
 
                //pairnei ta data apo to to response
                Response resp = Response.deserialize(result);
                if (resp.isSuccess() && resp.getData() != null) {
                    allResults.append(resp.getData());
                }
 
                System.out.println("Map result from Worker " + i + ": " + result);
 
            } catch (IOException e) {
                System.err.println("Worker " + i + " unavailable during search");
            }
        }
 
        // REDUCE phase stelnei ta apotelesmata
        Request req = Request.deserialize(raw);
        String reduced = sendToReducer(req.getType() + "|" + allResults.toString());
 
        return new Response(true, reduced, "Search complete").serialize();
    }

    public String sendToReducer(String data) {
 
        try {
            // anoigei TCP syndesi
            Socket reducerSocket = new Socket(reducerHost, reducerPort);
            
        
            DataOutputStream reducerOut = new DataOutputStream(reducerSocket.getOutputStream());
            reducerOut.writeUTF(data);
            reducerOut.flush();
 
            DataInputStream reducerIn = new DataInputStream(reducerSocket.getInputStream());
            String result = reducerIn.readUTF();
 
            reducerSocket.close();
 
            System.out.println("Reducer result: " + result);
            return result;
 
        } catch (IOException e) {
            System.err.println("Reducer unavailable: " + e.getMessage());
            // an o reducer den einai diathesimos
            return data;
        }
    }
 
}
