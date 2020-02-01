package com.practicaldime.router.core.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.router.core.handler.AppRouter;
import com.practicaldime.router.core.view.ViewEngine;
import com.practicaldime.router.core.view.ViewEngineFactory;

import java.util.Map;
import java.util.function.Consumer;

public interface Startable {

    String status();

    ViewEngine engine();

    ViewEngine initEngine(ViewEngineFactory engineFactory);

    Startable banner(String splash);

    Startable assets(String mapping, String folder);

    // ************* WORDPRESS *****************//
    Startable wordpress(String home, String proxyTo);

    // ************* CORS Headers *****************//
    Startable cors(Map<String, String> cors);

    // ************* START *****************//
    void listen(int port, String host);

    void listen(int port, String host, Consumer<String> result);

    // ************* STOP *****************//
    void shutdown();

    AppRouter getRoutes();

    ObjectMapper getObjectMapper();
}
