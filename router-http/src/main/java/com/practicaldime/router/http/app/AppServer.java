package com.practicaldime.router.http.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.router.core.config.HandlerConfig;
import com.practicaldime.router.core.handler.AppRouter;
import com.practicaldime.router.core.handler.RouteHandle;
import com.practicaldime.router.core.routing.MethodRouter;
import com.practicaldime.router.core.server.IServer;
import com.practicaldime.router.core.server.Restful;
import com.practicaldime.router.core.server.Startable;
import com.practicaldime.router.core.servlet.HandlerFilter;
import com.practicaldime.router.core.servlet.HandlerFunction;
import com.practicaldime.router.core.servlet.HandlerServlet;
import com.practicaldime.router.core.servlet.ObjectMapperSupplier;
import com.practicaldime.router.core.sse.EventsEmitter;
import com.practicaldime.router.core.view.ViewEngine;
import com.practicaldime.router.core.view.ViewEngineFactory;
import com.practicaldime.router.core.wsock.AppWsPolicy;
import com.practicaldime.router.core.wsock.AppWsProvider;
import org.apache.commons.cli.Options;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.http.HttpServlet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.practicaldime.router.core.server.AppOptions.applyDefaults;

public class AppServer implements IServer {

    private final Restful restful;
    private final Startable startable;
    private final Properties locals;

    private AppServer(Restful restful, Startable startable, Properties props) {
        this.restful = restful;
        this.startable = startable;
        this.locals = props;
    }

    public static IServer instance() {
        return instance(applyDefaults(new Options(), new String[]{}));
    }

    public static IServer instance(Map<String, String> props) {
        Properties locals = new Properties();
        props.forEach((key, value) -> locals.setProperty(key, value));
        Function<String, String> properties = key -> locals.getProperty(key);

        AppRouter router = new AppRouter(new MethodRouter());
        ServletContextHandler servlets = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ObjectMapper mapper = ObjectMapperSupplier.version1.get();
        ViewEngineFactory engineFactory = new ViewEngines();
        return instance(router, servlets, engineFactory, mapper, locals, properties);
    }

    public static IServer instance(AppRouter router, ServletContextHandler servlets, ViewEngineFactory engineFactory, ObjectMapper mapper, Properties locals, Function<String, String> properties) {
        return new AppServer(
                new RestfulImpl(servlets, properties, router, mapper),
                new StartableImpl(servlets, properties, engineFactory, new LinkedList<>()) {

                    @Override
                    public AppRouter getRoutes() {
                        return router;
                    }

                    @Override
                    public ObjectMapper getObjectMapper() {
                        return mapper;
                    }
                },
                locals);
    }

    @Override
    public final void use(String key, String value) {
        if (!this.locals.containsKey(key)) {
            this.locals.setProperty(key, value);
        }
    }

    @Override
    public Set<String> locals() {
        return locals.stringPropertyNames();
    }

    @Override
    public String locals(String param) {
        return locals.getProperty(param);
    }

    @Override
    public IServer cors(Map<String, String> cors) {
        this.locals.setProperty("cors", "true");
        if (cors != null) this.startable.cors(cors);
        return this;
    }

    @Override
    public void listen(int port, String host) {
        this.startable.listen(port, host);
    }

    @Override
    public void listen(int port, String host, Consumer<String> result) {
        this.startable.listen(port, host, result);
    }

    @Override
    public void shutdown() {
        this.startable.shutdown();
    }

    @Override
    public ViewEngine engine() {
        return this.startable.engine();
    }

    @Override
    public ViewEngine initEngine(ViewEngineFactory engineFactory) {
        return this.startable.initEngine(engineFactory);
    }

    @Override
    public AppRouter getRoutes() {
        return startable.getRoutes();
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return startable.getObjectMapper();
    }

    @Override
    public IServer engine(String engine) {
        this.locals.setProperty("engine", engine);
        return this;
    }

    @Override
    public IServer templates(String folder) {
        this.locals.setProperty("templates", folder);
        return this;
    }

    @Override
    public String status() {
        return startable.status();
    }

    @Override
    public IServer banner(String splash) {
        startable.banner(splash);
        return this;
    }

    @Override
    public IServer assets(String mapping, String folder) {
        startable.assets(mapping, folder);
        return this;
    }

    @Override
    public IServer wordpress(String home, String proxyTo) {
        this.startable.wordpress(home, proxyTo);
        return this;
    }

    @Override
    public String resolve(String path) {
        return this.restful.resolve(path);
    }

    @Override
    public IServer filter(HandlerFilter filter) {
        restful.filter(filter);
        return this;
    }

    @Override
    public IServer filter(String context, HandlerFilter filter) {
        restful.filter(context, filter);
        return this;
    }

    @Override
    public IServer servlet(String path, HandlerConfig config, HttpServlet handler) {
        restful.servlet(path, config, handler);
        return this;
    }

    @Override
    public IServer route(String method, String path, HandlerServlet handler) {
        this.restful.route(method, path, handler);
        return this;
    }

    @Override
    public IServer route(String method, String path, HandlerFunction handler) {
        this.restful.route(method, path, handler);
        return this;
    }

    @Override
    public IServer route(String method, String path, HandlerConfig config, HandlerFunction handler) {
        this.restful.route(method, path, config, handler);
        return this;
    }

    @Override
    public IServer route(String method, String path, HandlerConfig config, HandlerServlet handler) {
        this.restful.route(method, path, config, handler);
        return this;
    }

    @Override
    public IServer route(String method, String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        this.restful.route(method, path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer route(String method, String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        this.restful.route(method, path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer head(String path, HandlerServlet handler) {
        this.restful.head(path, handler);
        return this;
    }

    @Override
    public IServer head(String path, HandlerFunction handler) {
        this.restful.head(path, handler);
        return this;
    }

    @Override
    public IServer head(String path, HandlerConfig config, HandlerFunction handler) {
        this.restful.head(path, config, handler);
        return this;
    }

    @Override
    public IServer head(String path, HandlerConfig config, HandlerServlet handler) {
        this.restful.head(path, config, handler);
        return this;
    }

    @Override
    public IServer head(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        this.restful.head(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer head(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        this.restful.head(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer trace(String path, HandlerServlet handler) {
        this.restful.trace(path, handler);
        return this;
    }

    @Override
    public IServer trace(String path, HandlerFunction handler) {
        this.restful.trace(path, handler);
        return this;
    }

    @Override
    public IServer trace(String path, HandlerConfig config, HandlerServlet handler) {
        this.restful.trace(path, config, handler);
        return this;
    }

    @Override
    public IServer trace(String path, HandlerConfig config, HandlerFunction handler) {
        this.restful.trace(path, config, handler);
        return this;
    }

    @Override
    public IServer trace(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        this.restful.trace(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer trace(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        this.restful.trace(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer options(String path, HandlerServlet handler) {
        this.restful.options(path, handler);
        return this;
    }

    @Override
    public IServer options(String path, HandlerFunction handler) {
        this.restful.options(path, handler);
        return this;
    }

    @Override
    public IServer options(String path, HandlerConfig config, HandlerServlet handler) {
        this.restful.options(path, config, handler);
        return this;
    }

    @Override
    public IServer options(String path, HandlerConfig config, HandlerFunction handler) {
        this.restful.options(path, config, handler);
        return this;
    }

    @Override
    public IServer options(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        this.restful.options(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer options(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        this.restful.options(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer get(String path, HandlerServlet handler) {
        this.restful.get(path, handler);
        return this;
    }

    @Override
    public IServer get(String path, HandlerFunction handler) {
        this.restful.get(path, handler);
        return this;
    }

    @Override
    public IServer get(String path, HandlerConfig config, HandlerServlet handler) {
        this.restful.get(path, config, handler);
        return this;
    }

    @Override
    public IServer get(String path, HandlerConfig config, HandlerFunction handler) {
        this.restful.get(path, config, handler);
        return this;
    }

    @Override
    public IServer get(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        this.restful.get(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer get(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        this.restful.get(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer post(String path, HandlerServlet handler) {
        this.restful.post(path, handler);
        return this;
    }

    @Override
    public IServer post(String path, HandlerFunction handler) {
        this.restful.post(path, handler);
        return this;
    }

    @Override
    public IServer post(String path, HandlerConfig config, HandlerServlet handler) {
        this.restful.post(path, config, handler);
        return this;
    }

    @Override
    public IServer post(String path, HandlerConfig config, HandlerFunction handler) {
        this.restful.post(path, config, handler);
        return this;
    }

    @Override
    public IServer post(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        this.restful.post(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer post(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        this.restful.post(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer put(String path, HandlerServlet handler) {
        this.restful.put(path, handler);
        return this;
    }

    @Override
    public IServer put(String path, HandlerFunction handler) {
        this.restful.put(path, handler);
        return this;
    }

    @Override
    public IServer put(String path, HandlerConfig config, HandlerServlet handler) {
        this.restful.put(path, config, handler);
        return this;
    }

    @Override
    public IServer put(String path, HandlerConfig config, HandlerFunction handler) {
        this.restful.put(path, config, handler);
        return this;
    }

    @Override
    public IServer put(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        this.restful.put(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer put(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        this.restful.put(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer delete(String path, HandlerServlet handler) {
        this.restful.delete(path, handler);
        return this;
    }

    @Override
    public IServer delete(String path, HandlerFunction handler) {
        this.restful.delete(path, handler);
        return this;
    }

    @Override
    public IServer delete(String path, HandlerConfig config, HandlerServlet handler) {
        this.restful.delete(path, config, handler);
        return this;
    }

    @Override
    public IServer delete(String path, HandlerConfig config, HandlerFunction handler) {
        this.restful.delete(path, config, handler);
        return this;
    }

    @Override
    public IServer delete(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        this.restful.delete(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer delete(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        this.restful.delete(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer all(String path, HandlerServlet handler) {
        this.restful.all(path, handler);
        return this;
    }

    @Override
    public IServer all(String path, HandlerFunction handler) {
        this.restful.all(path, handler);
        return this;
    }

    @Override
    public IServer all(String path, HandlerConfig config, HandlerServlet handler) {
        this.restful.all(path, config, handler);
        return this;
    }

    @Override
    public IServer all(String path, HandlerConfig config, HandlerFunction handler) {
        this.restful.all(path, config, handler);
        return this;
    }

    @Override
    public IServer all(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        this.restful.all(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer all(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        this.restful.all(path, accept, type, config, handler);
        return this;
    }

    @Override
    public IServer websocket(String ctx, AppWsProvider provider) {
        this.restful.websocket(ctx, provider);
        return this;
    }

    @Override
    public IServer websocket(String ctx, AppWsProvider provider, AppWsPolicy policy) {
        this.restful.websocket(ctx, provider, policy);
        return this;
    }

    @Override
    public IServer subscribe(String path, HandlerConfig config, EventsEmitter eventsEmitter) {
        this.restful.subscribe(path, config, eventsEmitter);
        return this;
    }

    @Override
    public void accept(RouteHandle handle) {
        this.restful.accept(handle);
    }
}
