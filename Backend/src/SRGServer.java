package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class SRGServer {

    private int port;
    private static HashMap<String, SRGBuffer>  buffers   = new HashMap<>();
    private static HashMap<String, ProdThread> producers = new HashMap<>();
    private static final int BUF_SIZE = 100;

    public SRGServer(int port) {
        this.port = port;
    }

    public static synchronized void registerGame(String secret) {
        if (!buffers.containsKey(secret)) {
            SRGBuffer buf = new SRGBuffer(BUF_SIZE);
            ProdThread prod = new ProdThread(buf);
            prod.setDaemon(true);
            prod.start();
            buffers.put(secret, buf);
            producers.put(secret, prod);
            System.out.println("SRG: new generator registered for a game");
        }
    }

    public static SRGBuffer getBuffer(String secret) {
        return buffers.get(secret);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("SRG Server started at port " + port);

            while (true) {
                Socket workerSocket = serverSocket.accept();
                ConsHandler handler = new ConsHandler(workerSocket);
                handler.setDaemon(true);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        new SRGServer(port).start();
    }
}