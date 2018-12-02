package com.practicaldime.zesty.extras;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;

public interface AppWsListener {
    
    void onConnect(Session sess) throws IOException;
    
    void onString(Session sess, String message) throws IOException;
    
     void onBinary(Session sess, byte[] payload, int offset, int len) throws IOException;
    
    void onClose(Session sess, int statusCode, String reason) throws IOException;
    
    void onError(Throwable cause);
    
    void sendString(String message) throws IOException;
    
    void sendPartial(String message, Boolean isLast) throws IOException;
    
    void close();
    
    String timestamp();
}
