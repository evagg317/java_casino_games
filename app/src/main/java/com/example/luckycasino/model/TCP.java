package com.example.luckycasino.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Ο MasterClient είναι υπεύθυνος για την TCP επικοινωνία με τον Master.
 * Αντικαθιστά τον κώδικα TCP του PlayerApp.java από το Παραδοτέο Α.
 */
public class TCP {

    //private static final String MASTER_HOST = "192.168.1.1";
    private static final String MASTER_HOST = "10.0.2.2";
    private static final int MASTER_PORT = 8080;

    /**
     * Στέλνει ένα serialized request στον Master και επιστρέφει το raw response.
     * Ίδιος κώδικας με τον PlayerApp.java του Παραδοτέου Α.
     * @param serializedRequest το request σε string format (από Request.serialize())
     * @return το raw response string (για Response.deserialize())
     * @throws Exception αν αποτύχει η σύνδεση
     */
    public String sendRequest(String serializedRequest) throws Exception {
        Socket s = new Socket(MASTER_HOST, MASTER_PORT);

        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        DataInputStream in = new DataInputStream(s.getInputStream());

        // στέλνει το request
        out.writeUTF(serializedRequest);
        out.flush();

        // παίρνει το response
        String response = in.readUTF();

        s.close();
        return response;
    }
}
