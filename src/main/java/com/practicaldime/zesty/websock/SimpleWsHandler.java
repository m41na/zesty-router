package com.practicaldime.zesty.websock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

public class SimpleWsHandler extends AppWsAdapter {

    private static final Map<SessionId, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();
    private final String context;
    
	public SimpleWsHandler(String context) {
		super();
		this.context = context;
	}

	@Override
	public String getContext() {
		return this.context;
	}

	@Override
	public Function<Session, SessionId> idStrategy() {
		return (sess) -> {
	        String url = sess.getUpgradeRequest().getRequestURI().toString();
	        String regex = String.format("\\/%s\\/(.+?)\\/(.+?)$", getContext());
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(url);
	        if (matcher.find()) {
	            String topic = matcher.group(1);
	            String user = matcher.group(2);
	            return new SessionId(getContext(), topic, user);
            }
	        else{
	            sess.close(400, "Could nor create a session id");
	            return null;
            }
		};
	}

	@Override
    public void onConnect() throws IOException {
        SessionId sessionId = sessionId();
        AppWsMessage msg = new AppWsMessage("server", sessionId.user, timestamp(), "Hi " + sessionId.user + "! You are now online");
        getRemote().sendString(GSON.toJson(msg));
        SESSIONS.put(sessionId, getSession());

    }

    @Override
    public void onString(String message) throws IOException {
        String dateTime = timestamp();

        AppWsMessage incoming = GSON.fromJson(message, AppWsMessage.class);
        String from = incoming.from;
        SessionId fromId = sessionId();
        SessionId to = fromId.copy(incoming.to);

        AppWsMessage outgoing = new AppWsMessage(from, to.user, dateTime, incoming.message);
        
        if(to.user != null && to.user .trim().length() > 0) {
	        Session toSession = SESSIONS.get(to); 
	        toSession.getRemote().sendString(GSON.toJson(outgoing));
	        //echo back to sender
	        getRemote().sendString(GSON.toJson(outgoing));
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
    public void onClose(int statusCode, String reason) {
    	log("info", "the client closed the connection");
        if(isConnected()) {
        	getSession().close();
        }
        SessionId key = sessionId();
        if(key != null) SESSIONS.remove(key);
    }

    @Override
    public void onError(Throwable cause) {
    	log("error", "the server will close all connections now -> " + cause.getMessage());
        if(isConnected()) {
        	getSession().close(500, cause.getMessage());
        }
        SessionId key = sessionId();
        if(key != null) SESSIONS.remove(key);
    }
    
    @Override
    public String timestamp() {
        return new SimpleDateFormat("dd MMM, yy 'at' mm:hh:ssa").format(new Date());
    }

    static class SessionId implements Comparable<SessionId> {

        final String context;
        final String topic;
        final String user;

        SessionId(String context, String topic, String user) {
            this.context = context;
            this.topic = topic;
            this.user = user;
        }

        public SessionId copy(String user){
            return new SessionId(this.context, this.topic, user);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SessionId)) return false;
            SessionId that = (SessionId) o;
            return context.equals(that.context) &&
                    topic.equals(that.topic) &&
                    user.equals(that.user);
        }

        @Override
        public int hashCode() {
            return Objects.hash(context, topic, user);
        }

        @Override
        public int compareTo(SessionId that) {
            if(this == that ) return 0;
            int comparison = context.compareTo(that.context);
            if(comparison != 0) return comparison;
            comparison = topic.compareTo(that.topic);
            if(comparison != 0) return comparison;
            return user.compareTo(that.user);
        }
    }
}
