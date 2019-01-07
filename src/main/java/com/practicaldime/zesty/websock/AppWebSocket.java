package com.practicaldime.zesty.websock;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppWebSocket extends WebSocketAdapter implements AppWsListener{

    private static final Logger LOG = LoggerFactory.getLogger(AppWebSocket.class);
    private AppWsListener wsEvents;

    public AppWebSocket(AppWsListener wsEvents) {
        this.wsEvents = wsEvents;
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        try {
            onConnect(sess);
        } catch (IOException ex) {
            LOG.error("onWebSocketConnect () error", ex);
        }
    }

    @Override
    public void onWebSocketText(String json) {
        super.onWebSocketText(json);
        try {
            onString(getSession(), json);
        } catch (IOException ex) {
            LOG.error("onWebSocketText() error", ex);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        try {
            LOG.warn("Socket Closed: {}", reason);
            onClose(getSession(), statusCode, reason);
        } catch (IOException ex) {
            LOG.error("onWebSocketText() error", ex);
        }
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        LOG.error("Socket error", cause);
        this.wsEvents.onError(cause);
    }

    @Override
    public void onConnect(Session sess) throws IOException {
        this.wsEvents.onConnect(sess);
    }

    @Override
    public void onString(Session sess, String message) throws IOException {
        this.wsEvents.onString(sess, message);
    }

    @Override
    public void onBinary(Session sess, byte[] payload, int offset, int len) throws IOException {
        this.wsEvents.onBinary(sess, payload, offset, len);
    }

    @Override
    public void onClose(Session sess, int statusCode, String reason) throws IOException {
        this.wsEvents.onClose(sess, statusCode, reason);
        this.wsEvents = null;
    }

    @Override
    public void onError(Throwable cause) {
        this.wsEvents.onError(cause);
    }

    @Override
    public void sendString(String message) throws IOException {
        getRemote().sendString(message);
    }

    @Override
    public void sendPartial(String message, Boolean isLast) throws IOException {
        getRemote().sendPartialString(message, isLast);
    }

    @Override
    public String timestamp() {
        return this.wsEvents.timestamp();
    }

    @Override
    public void close() {
        getSession().close();
    }
}
