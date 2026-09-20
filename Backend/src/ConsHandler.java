package src;

import java.io.*;
import java.net.Socket;

public class ConsHandler extends Thread {

    private Socket workerSocket;

    public ConsHandler(Socket workerSocket) {
        this.workerSocket = workerSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream  in  = new DataInputStream(workerSocket.getInputStream());
            PrintWriter out = new PrintWriter(workerSocket.getOutputStream(), true);

            String secret = in.readUTF();
            System.out.println("SRG: request received for a game");

            SRGServer.registerGame(secret);
            SRGBuffer buffer = SRGServer.getBuffer(secret);

            int num = buffer.dequeue();
            String hash = SHA.hash(num + secret);
            out.println(num + " " + hash);

        } catch (IOException e) {
            System.err.println("ConsHandler error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            try { workerSocket.close(); } catch (IOException ignored) {}
        }
    }
}