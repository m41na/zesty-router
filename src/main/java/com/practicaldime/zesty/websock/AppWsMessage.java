package com.practicaldime.zesty.websock;

public class AppWsMessage {

    public String from;
    public String to;
    public String time;
    public String message;
    public String error;
    public int status;
    
    public AppWsMessage(String from, String to, String time, String message) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.message = message;
        this.status = 200;
    }
    
    public AppWsMessage(String from, String to, String time, String error, int status) {
        this(from, to, time, null);
        this.error = error;
        this.status = status;
    }
}
