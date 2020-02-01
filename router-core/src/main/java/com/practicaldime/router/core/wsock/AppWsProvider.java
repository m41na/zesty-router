package com.practicaldime.router.core.wsock;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public interface AppWsProvider<T extends WebSocketAdapter> {

    T provide();
}
