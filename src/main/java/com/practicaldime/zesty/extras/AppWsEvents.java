package com.practicaldime.zesty.extras;

import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.websocket.api.Session;

public class AppWsEvents implements AppWsListener {

    private static final Map<String, Session> USERS = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();
    private String user;
    private Session sess;

    @Override
    public void onConnect(Session sess) throws IOException {
        String from = null, to = null;
        String url = sess.getUpgradeRequest().getRequestURI().toString();
        Pattern pattern = Pattern.compile("(/events/(.+?)/to/(.*))$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            from = matcher.group(2);
            to = matcher.group(3);
        }

        AppWsMessage msg = new AppWsMessage("server", from, timestamp(), "Hi " + from + "! You are now online");
        sess.getRemote().sendString(msg.toString());
        USERS.put(from, sess);
        this.user = from;
        this.sess = sess;

    }

    @Override
    public void onString(Session sess, String message) throws IOException {
        String dateTime = timestamp();

        AppWsMessage incoming = GSON.fromJson(message, AppWsMessage.class);
        String from = incoming.from;
        String to = incoming.to;
        AppWsMessage echo = new AppWsMessage(from, "server", dateTime, incoming.message);
        sendString(GSON.toJson(echo));

        Session dest = USERS.get(to);
        AppWsMessage outgoing = new AppWsMessage(from, to, dateTime, incoming.message);
        dest.getRemote().sendString(GSON.toJson(outgoing));
    }

    @Override
    public void onBinary(org.eclipse.jetty.websocket.api.Session sess, byte[] payload, int offset, int len) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onClose(Session session, int statusCode, String reason) throws IOException {
        if (USERS.keySet().contains(this.user)) {
            USERS.remove(this.user);
        }
        this.sess = null;
    }

    @Override
    public void onError(Throwable cause) {
        if (USERS.keySet().contains(this.user)) {
            USERS.remove(this.user);
        }
        this.sess = null;
    }

    @Override
    public void sendString(String message) throws IOException {
        this.sess.getRemote().sendString(message);
    }

    @Override
    public void sendPartial(String message, Boolean isLast) throws IOException {
        this.sess.getRemote().sendPartialString(message, isLast);
    }

    @Override
    public String timestamp() {
        return new SimpleDateFormat("dd MMM, yy 'at' mm:hh:ssa").format(new Date());
    }

    @Override
    public void close() {
        this.sess.close();
        this.sess = null;
    }
}
