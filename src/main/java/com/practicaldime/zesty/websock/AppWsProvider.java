package com.practicaldime.zesty.websock;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public interface AppWsProvider<T extends WebSocketAdapter> {
    
    T provide();
}
