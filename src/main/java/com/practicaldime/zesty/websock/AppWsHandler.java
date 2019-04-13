package com.practicaldime.zesty.websock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

public class AppWsHandler extends AbstractWsAdapter {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();
    private final String context;
    
	public AppWsHandler(String context) {
		super();
		this.context = context;
	}

	@Override
	public String getContext() {
		return this.context;
	}

	@Override
	public Function<Session, String> sessionIdStrategy() {
		return (sess) -> {
	        String url = sess.getUpgradeRequest().getRequestURI().toString();
	        String regex = String.format("/%s/(.+?)$", getContext());
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(url);
	        return (matcher.find()) ? matcher.group(1) : null;
		};
	}

	@Override
    public void onConnect(Session sess) throws IOException {
        String sessionId = sessionId();
        AppWsMessage msg = new AppWsMessage("server", sessionId, timestamp(), "Hi " + sessionId + "! You are now online");
        sess.getRemote().sendString(GSON.toJson(msg));
        SESSIONS.put(sessionId, sess);

    }

    @Override
    public void onString(Session sess, String message) throws IOException {
        String dateTime = timestamp();

        AppWsMessage incoming = GSON.fromJson(message, AppWsMessage.class);
        String from = incoming.from;
        String to = incoming.to;

        AppWsMessage outgoing = new AppWsMessage(from, to, dateTime, incoming.message);
        
        if(to != null && to .trim().length() > 0) {
	        Session toSession = SESSIONS.get(to); 
	        toSession.getRemote().sendString(GSON.toJson(outgoing));
	        //echo back to sender
	        sess.getRemote().sendString(GSON.toJson(outgoing));
        }
        else {
        	SESSIONS.values().forEach(session -> {
        		try {
					session.getRemote().sendString(GSON.toJson(outgoing));
				} catch (IOException e) {
					log("error", e.getMessage());
				}
        	});
        }
    }

    @Override
    public void onClose(Session sess, int statusCode, String reason) throws IOException {
    	log("info", "the client closed the connection");
        if(sess.isOpen()) {
        	sess.close();
        }
    	SESSIONS.remove(sessionId());
    }

    @Override
    public void onError(Session sess, Throwable cause) {
    	log("error", "the server will close all connections now -> " + cause.getMessage());
        if(sess.isOpen()) {
        	sess.close(500, cause.getMessage());
        }
        SESSIONS.remove(sessionId());
    }
    
    @Override
    public String timestamp() {
        return new SimpleDateFormat("dd MMM, yy 'at' mm:hh:ssa").format(new Date());
    }
}
