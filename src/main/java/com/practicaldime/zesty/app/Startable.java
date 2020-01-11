package com.practicaldime.zesty.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.zesty.basics.AppRouter;
import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.ViewEngineFactory;

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
