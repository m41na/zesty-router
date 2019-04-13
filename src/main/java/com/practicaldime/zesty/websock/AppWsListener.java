package com.practicaldime.zesty.websock;

import java.io.IOException;
import java.util.function.Function;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

public interface AppWsListener {
	
	String getContext();
	
	RemoteEndpoint getRemote();

    Session getSession();

    boolean isConnected();

    boolean isNotConnected();
	
	String sessionId();
	
	Function<Session, String> sessionIdStrategy();
    
    void onConnect(Session sess) throws IOException;
    
    void onString(Session sess, String message) throws IOException;
    
    void onBinary(Session sess, byte[] payload, int offset, int len) throws IOException;
    
    void onClose(Session sess, int statusCode, String reason) throws IOException;
    
    void onError(Session sess, Throwable cause) throws IOException;;
    
    void sendString(String message) throws IOException;
    
    void sendPartial(String message, Boolean isLast) throws IOException;
    
    void close();
    
    String timestamp();
}
