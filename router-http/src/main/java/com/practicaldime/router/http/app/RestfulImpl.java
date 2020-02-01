package com.practicaldime.router.http.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.router.core.config.HandlerConfig;
import com.practicaldime.router.core.handler.AppRouter;
import com.practicaldime.router.core.handler.RouteHandle;
import com.practicaldime.router.core.routing.Routing;
import com.practicaldime.router.core.server.Restful;
import com.practicaldime.router.core.sse.AppEventSource;
import com.practicaldime.router.core.sse.EventsEmitter;
import com.practicaldime.router.core.wsock.AppWsPolicy;
import com.practicaldime.router.core.wsock.AppWsProvider;
import com.practicaldime.router.core.wsock.AppWsServlet;
import com.practicaldime.router.core.servlet.*;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.EnumSet;
import java.util.function.Function;

public class RestfulImpl implements Restful {

    private final AppRouter routes;
    private final ObjectMapper mapper;
    private final ServletContextHandler servlets;
    private final Function<String, String> properties;

    public RestfulImpl(ServletContextHandler servlets, Function<String, String> properties, AppRouter routes, ObjectMapper mapper) {
        this.routes = routes;
        this.servlets = servlets;
        this.properties = properties;
        this.mapper = mapper;
    }

    @Override
    public String resolve(String path) {
        String appctx = properties.apply("appctx");
        String path1 = !appctx.startsWith("/") ? "/" + appctx : appctx;
        if (path1.endsWith("/")) {
            path1 = path1.substring(0, path1.length() - 1);
        }
        String path2 = !path.startsWith("/") ? "/" + path : path;
        return path1 + path2;
    }

    @Override
    public Restful filter(HandlerFilter filter) {
        return filter("/*", filter);
    }

    @Override
    public Restful filter(String context, HandlerFilter filter) {
        FilterHolder holder = new FilterHolder(filter);
        servlets.addFilter(holder, context, EnumSet.of(DispatcherType.REQUEST));
        return this;
    }

    @Override
    public Restful servlet(String path, HandlerConfig config, HttpServlet handler) {
        Routing.Route route = new Routing.Route(resolve(path), "all", "*", "*");
        route.setId();
        routes.add(route);
        // add servlet handler
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servlets.addServlet(holder, route.rid);
        return this;
    }

    @Override
    public Restful route(String method, String path, HandlerServlet handler) {
        return route(method, path, null, handler);
    }

    @Override
    public Restful route(String method, String path, HandlerFunction handler) {
        return route(method, path, null, handler);
    }

    @Override
    public Restful route(String method, String path, HandlerConfig config, HandlerFunction handler) {
        return route(method, path, config, handler);
    }

    @Override
    public Restful route(String method, String path, HandlerConfig config, HandlerServlet handler) {
        return route(method, path, "", "", config, handler);
    }

    @Override
    public Restful route(String method, String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        return route(method, path, accept, type, config, new HandlerServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
                handler.apply(request, response, promise);
            }
        });
    }

    @Override
    public Restful route(String method, String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        switch (method.toLowerCase()) {
            case "get":
                return get(path, accept, type, config, handler);
            case "post":
                return post(path, accept, type, config, handler);
            case "put":
                return put(path, accept, type, config, handler);
            case "delete":
                return delete(path, accept, type, config, handler);
            case "options":
                return options(path, accept, type, config, handler);
            case "trace":
                return trace(path, accept, type, config, handler);
            case "head":
                return head(path, accept, type, config, handler);
            case "all":
            case "*":
                return all(path, accept, type, config, handler);
            default:
                throw new UnsupportedOperationException(method + " is not a supported method");
        }
    }

    // ************* HEAD *****************//
    @Override
    public Restful head(String path, HandlerServlet handler) {
        return head(path, "", "", null, handler);
    }

    @Override
    public Restful head(String path, HandlerFunction handler) {
        return head(path, "", "", null, handler);
    }

    @Override
    public Restful head(String path, HandlerConfig config, HandlerFunction handler) {
        return head(path, "", "", config, handler);
    }

    @Override
    public Restful head(String path, HandlerConfig config, HandlerServlet handler) {
        return head(path, "", "", config, handler);
    }

    @Override
    public Restful head(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        return head(path, accept, type, config, new HandlerServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
                handler.apply(request, response, promise);
            }
        });
    }

    @Override
    public Restful head(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        Routing.Route route = new Routing.Route(resolve(path), "head", accept, type);
        route.setId();
        routes.add(route);
        // add servlet handler
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servlets.addServlet(holder, route.rid);
        return this;
    }

    // ************* TRACE *****************//
    @Override
    public Restful trace(String path, HandlerServlet handler) {
        return trace(path, "", "", null, handler);
    }

    @Override
    public Restful trace(String path, HandlerFunction handler) {
        return trace(path, "", "", null, handler);
    }

    @Override
    public Restful trace(String path, HandlerConfig config, HandlerServlet handler) {
        return trace(path, "", "", config, handler);
    }

    @Override
    public Restful trace(String path, HandlerConfig config, HandlerFunction handler) {
        return trace(path, "", "", config, handler);
    }

    @Override
    public Restful trace(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        return trace(path, accept, type, config, new HandlerServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
                handler.apply(request, response, promise);
            }
        });
    }

    @Override
    public Restful trace(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        Routing.Route route = new Routing.Route(resolve(path), "trace", accept, type);
        route.setId();
        routes.add(route);
        // add servlet handler
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servlets.addServlet(holder, route.rid);
        return this;
    }

    // ************* OPTIONS *****************//
    @Override
    public Restful options(String path, HandlerServlet handler) {
        return options(path, "", "", null, handler);
    }

    @Override
    public Restful options(String path, HandlerFunction handler) {
        return options(path, "", "", null, handler);
    }

    @Override
    public Restful options(String path, HandlerConfig config, HandlerServlet handler) {
        return options(path, "", "", config, handler);
    }

    @Override
    public Restful options(String path, HandlerConfig config, HandlerFunction handler) {
        return options(path, "", "", config, handler);
    }

    @Override
    public Restful options(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        return options(path, accept, type, config, new HandlerServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
                handler.apply(request, response, promise);
            }
        });
    }

    @Override
    public Restful options(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        Routing.Route route = new Routing.Route(resolve(path), "options", accept, type);
        route.setId();
        routes.add(route);
        // add servlet handler
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servlets.addServlet(holder, route.rid);
        return this;
    }

    // ************* GET *****************//
    @Override
    public Restful get(String path, HandlerServlet handler) {
        return get(path, "", "", null, handler);
    }

    @Override
    public Restful get(String path, HandlerFunction handler) {
        return get(path, "", "", null, handler);
    }

    @Override
    public Restful get(String path, HandlerConfig config, HandlerServlet handler) {
        return get(path, "", "", config, handler);
    }

    @Override
    public Restful get(String path, HandlerConfig config, HandlerFunction handler) {
        return get(path, "", "", config, handler);
    }

    @Override
    public Restful get(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        return get(path, accept, type, config, new HandlerServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
                handler.apply(request, response, promise);
            }
        });
    }

    @Override
    public Restful get(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        Routing.Route route = new Routing.Route(resolve(path), "get", accept, type);
        route.setId();
        routes.add(route);
        // add servlet handler
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servlets.addServlet(holder, route.rid);
        return this;
    }

    // ************* POST *****************//
    @Override
    public Restful post(String path, HandlerServlet handler) {
        return post(path, "", "", null, handler);
    }

    @Override
    public Restful post(String path, HandlerFunction handler) {
        return post(path, "", "", null, handler);
    }

    @Override
    public Restful post(String path, HandlerConfig config, HandlerServlet handler) {
        return post(path, "", "", config, handler);
    }

    @Override
    public Restful post(String path, HandlerConfig config, HandlerFunction handler) {
        return post(path, "", "", config, handler);
    }

    @Override
    public Restful post(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        return post(path, accept, type, config, new HandlerServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
                handler.apply(request, response, promise);
            }
        });
    }

    @Override
    public Restful post(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        Routing.Route route = new Routing.Route(resolve(path), "post", accept, type);
        route.setId();
        routes.add(route);
        // add servlet handler
        ServletHolder holder = new ServletHolder(handler);
        // for multipart/form-data, customize the servlet holder
        if (type.toLowerCase().contains("multipart/form-data")) {
            MultipartConfigElement mpce = new MultipartConfigElement("temp", 1024 * 1024 * 50, 1024 * 1024, 5);
            holder.getRegistration().setMultipartConfig(mpce);
        }
        if (config != null) config.configure(holder);
        servlets.addServlet(holder, route.rid);
        return this;
    }

    // ************* PUT *****************//
    @Override
    public Restful put(String path, HandlerServlet handler) {
        return put(path, "", "", null, handler);
    }

    @Override
    public Restful put(String path, HandlerFunction handler) {
        return put(path, "", "", null, handler);
    }

    @Override
    public Restful put(String path, HandlerConfig config, HandlerServlet handler) {
        return put(path, "", "", config, handler);
    }

    @Override
    public Restful put(String path, HandlerConfig config, HandlerFunction handler) {
        return put(path, "", "", config, handler);
    }

    @Override
    public Restful put(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        return put(path, accept, type, config, new HandlerServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
                handler.apply(request, response, promise);
            }
        });
    }

    @Override
    public Restful put(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        Routing.Route route = new Routing.Route(resolve(path), "put", accept, type);
        route.setId();
        routes.add(route);
        // add servlet handler
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servlets.addServlet(holder, route.rid);
        return this;
    }

    // ************* DELETE *****************//
    @Override
    public Restful delete(String path, HandlerServlet handler) {
        return delete(path, "", "", null, handler);
    }

    @Override
    public Restful delete(String path, HandlerFunction handler) {
        return delete(path, "", "", null, handler);
    }

    @Override
    public Restful delete(String path, HandlerConfig config, HandlerServlet handler) {
        return delete(path, "", "", config, handler);
    }

    @Override
    public Restful delete(String path, HandlerConfig config, HandlerFunction handler) {
        return delete(path, "", "", config, handler);
    }

    @Override
    public Restful delete(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        return delete(path, accept, type, config, new HandlerServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
                handler.apply(request, response, promise);
            }
        });
    }

    @Override
    public Restful delete(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        Routing.Route route = new Routing.Route(resolve(path), "delete", accept, type);
        route.setId();
        routes.add(route);
        // add servlet handler
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servlets.addServlet(holder, route.rid);
        return this;
    }

    // ************* ALL *****************//
    @Override
    public Restful all(String path, HandlerServlet handler) {
        return all(path, "", "", null, handler);
    }

    @Override
    public Restful all(String path, HandlerFunction handler) {
        return all(path, "", "", null, handler);
    }

    @Override
    public Restful all(String path, HandlerConfig config, HandlerServlet handler) {
        return all(path, "", "", config, handler);
    }

    @Override
    public Restful all(String path, HandlerConfig config, HandlerFunction handler) {
        return all(path, "", "", config, handler);
    }

    @Override
    public Restful all(String path, String accept, String type, HandlerConfig config, HandlerFunction handler) {
        return all(path, accept, type, config, new HandlerServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
                handler.apply(request, response, promise);
            }
        });
    }

    @Override
    public Restful all(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
        Routing.Route route = new Routing.Route(resolve(path), "all", accept, type);
        route.setId();
        routes.add(route);
        // add servlet handler
        ServletHolder holder = new ServletHolder(handler);
        if (config != null) config.configure(holder);
        servlets.addServlet(holder, route.rid);
        return this;
    }// ************* WEBSOCKETS *****************//

    @Override
    public Restful websocket(String ctx, AppWsProvider provider) {
        return websocket(ctx, provider, AppWsPolicy::defaultConfig);
    }

    @Override
    public Restful websocket(String ctx, AppWsProvider provider, AppWsPolicy policy) {
        // Add a websocket dest a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", new AppWsServlet(provider, policy.getPolicy()));
        servlets.addServlet(holderEvents, ctx);
        return this;
    }

    // ************* SSE *****************//
    @Override
    public Restful subscribe(String path, HandlerConfig config, EventsEmitter eventsEmitter) {
        return servlet(path, config, new EventSourceServlet() {
            @Override
            protected EventSource newEventSource(HttpServletRequest request) {
                return new AppEventSource(request) {

                    @Override
                    public void onOpen(Emitter emitter) throws IOException {
                        super.onOpen(emitter);
                        eventsEmitter.onOpen(mapper, emitter);
                    }
                };
            }
        });
    }

    // ************* Accept new Handler ************** //
    @Override
    public void accept(RouteHandle handle) {
        Routing.Route route = new Routing.Route(resolve(handle.getPath()), handle.getMethod(), handle.getAccept(), handle.getType());
        route.setId();
        routes.add(route);
        // add servlet handler
        ServletHolder holder = handle.handler(handle.start());
        handle.getConfig().configure(holder);
        servlets.addServlet(holder, route.rid);
    }
}
