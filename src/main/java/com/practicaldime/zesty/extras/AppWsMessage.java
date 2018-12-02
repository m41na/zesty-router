package com.practicaldime.zesty.extras;

public class AppWsMessage {

    public String from;
    public String to;
    public String time;
    public String message;

    public AppWsMessage(String from, String to, String time, String message) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.message = message;
    }
}
