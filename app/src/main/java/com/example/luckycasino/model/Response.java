package com.example.luckycasino.model;

public class Response {
    private boolean success;
    private String data;
    private String message;

    public Response(boolean success, String data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }
    public String  getData() {
        return data;
    }
    public String  getMessage() {
        return message;
    }


    public String serialize() {
        String d = "";
        String m = "";

        if (data != null) d = data;
        if (message != null) m = message;

        if (success) {
            return "1|" + d + "|" + m;
        } else {
            return "0|" + d + "|" + m;
        }
    }


    public static Response deserialize(String raw) {

        if (raw == null || raw.isEmpty()) {
            return new Response(false, "", "Empty response from server");
        }
        String[] parts = raw.split("\\|", 3);
        boolean success = parts[0].equals("1");
        String data = (parts.length > 1) ? parts[1] : "";
        String message = (parts.length > 2) ? parts[2] : "";

        return new Response(success, data, message);
    }

    @Override
    public String toString() {
        return "Response{success =" + success + ", message ='" + message + "', data ='" + data + "'}";
    }
}
