package com.practicaldime.zesty.websock;

import java.util.ArrayList;
import java.util.List;

public class AppWsMessage {

	public String type;
    public String from;
    public String to;
    public String time;
    public String message;
    public String error;
    public List<String> active = new ArrayList<>();
    
    public AppWsMessage(String from, String to, String time, String message) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.message = message;
    }

	@Override
	public String toString() {
		return "AppWsMessage [type=" + type + ", from=" + from + ", to=" + to + ", time=" + time + ", message="
				+ message + ", error=" + error + ", active=" + active + "]";
	}
}
