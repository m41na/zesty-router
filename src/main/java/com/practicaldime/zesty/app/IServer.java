package com.practicaldime.zesty.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.zesty.basics.AppRouter;
import com.practicaldime.zesty.basics.RouteHandle;
import com.practicaldime.zesty.servlet.HandlerConfig;
import com.practicaldime.zesty.servlet.HandlerFilter;
import com.practicaldime.zesty.servlet.HandlerFunction;
import com.practicaldime.zesty.servlet.HandlerServlet;
import com.practicaldime.zesty.sse.EventsEmitter;
import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.ViewEngineFactory;
import com.practicaldime.zesty.websock.AppWsPolicy;
import com.practicaldime.zesty.websock.AppWsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface IServer {

    Logger LOG = LoggerFactory.getLogger(AppServer.class);

    void use(String key, String value);

    Set<String> locals();

    String locals(String param);

    IServer cors(Map<String, String> cors);

    void listen(int port, String host);

    void listen(int port, String host, Consumer<String> result);

    void shutdown();

    ViewEngine engine();

    ViewEngine initEngine(ViewEngineFactory engineFactory);

    AppRouter getRoutes();

    ObjectMapper getObjectMapper();

    IServer engine(String engine);

    IServer templates(String folder);

    String status();

    IServer banner(String splash);

    IServer assets(String mapping, String folder);

    IServer wordpress(String home, String proxyTo);

    String resolve(String path);

    IServer filter(HandlerFilter filter);

    IServer filter(String context, HandlerFilter filter);

    IServer servlet(String path, HandlerConfig config, HttpServlet handler);

    IServer route(String method, String path, HandlerServlet handler);

    IServer route(String method, String path, HandlerFunction handler);

    IServer route(String method, String path, HandlerConfig config, HandlerFunction handler);

    IServer route(String method, String path, HandlerConfig config, HandlerServlet handler);

    IServer route(String method, String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    IServer route(String method, String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    IServer head(String path, HandlerServlet handler);

    IServer head(String path, HandlerFunction handler);

    IServer head(String path, HandlerConfig config, HandlerFunction handler);

    IServer head(String path, HandlerConfig config, HandlerServlet handler);

    IServer head(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    IServer head(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    IServer trace(String path, HandlerServlet handler);

    IServer trace(String path, HandlerFunction handler);

    IServer trace(String path, HandlerConfig config, HandlerServlet handler);

    IServer trace(String path, HandlerConfig config, HandlerFunction handler);

    IServer trace(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    IServer trace(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    IServer options(String path, HandlerServlet handler);

    IServer options(String path, HandlerFunction handler);

    IServer options(String path, HandlerConfig config, HandlerServlet handler);

    IServer options(String path, HandlerConfig config, HandlerFunction handler);

    IServer options(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    IServer options(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    IServer get(String path, HandlerServlet handler);

    IServer get(String path, HandlerFunction handler);

    IServer get(String path, HandlerConfig config, HandlerServlet handler);

    IServer get(String path, HandlerConfig config, HandlerFunction handler);

    IServer get(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    IServer get(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    IServer post(String path, HandlerServlet handler);

    IServer post(String path, HandlerFunction handler);

    IServer post(String path, HandlerConfig config, HandlerServlet handler);

    IServer post(String path, HandlerConfig config, HandlerFunction handler);

    IServer post(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    IServer post(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    IServer put(String path, HandlerServlet handler);

    IServer put(String path, HandlerFunction handler);

    IServer put(String path, HandlerConfig config, HandlerServlet handler);

    IServer put(String path, HandlerConfig config, HandlerFunction handler);

    IServer put(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    IServer put(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    IServer delete(String path, HandlerServlet handler);

    IServer delete(String path, HandlerFunction handler);

    IServer delete(String path, HandlerConfig config, HandlerServlet handler);

    IServer delete(String path, HandlerConfig config, HandlerFunction handler);

    IServer delete(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    IServer delete(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    IServer all(String path, HandlerServlet handler);

    IServer all(String path, HandlerFunction handler);

    IServer all(String path, HandlerConfig config, HandlerServlet handler);

    IServer all(String path, HandlerConfig config, HandlerFunction handler);

    IServer all(String path, String accept, String type, HandlerConfig config, HandlerFunction handler);

    IServer all(String path, String accept, String type, HandlerConfig config, HandlerServlet handler);

    IServer websocket(String ctx, AppWsProvider provider);

    IServer websocket(String ctx, AppWsProvider provider, AppWsPolicy policy);

    IServer subscribe(String path, HandlerConfig config, EventsEmitter eventsEmitter);

    void accept(RouteHandle handle);
}
