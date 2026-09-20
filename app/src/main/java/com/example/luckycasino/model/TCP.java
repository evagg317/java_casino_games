package com.example.luckycasino.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TCP {
public static final String MASTER_HOST = "10.0.2.2";
    private static final int MASTER_PORT = 8080;

    //stelnei serialized request ston master
    public String sendRequest(String serializedRequest) throws Exception {
        Socket s = new Socket(MASTER_HOST, MASTER_PORT);

        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        DataInputStream in = new DataInputStream(s.getInputStream());

        //stelnei to request
        out.writeUTF(serializedRequest);
        out.flush();

        //pairnei to response
        String response = in.readUTF();

        s.close();
        return response;
    }
}
