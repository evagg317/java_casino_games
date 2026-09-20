package src;
 
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
 
public class ReducerMain {
 
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]); 
        try (ServerSocket serverSocket = new ServerSocket(port);)
        {            
            System.out.println("Reducer started at port " + port);
 
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Reducer: Master connected");
 
                new HandleThreadReducer(client).start();
            }
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}