package com.practicaldime.zesty.basics;

import com.practicaldime.zesty.servlet.*;
import org.eclipse.jetty.servlet.ServletHolder;

public abstract class RouteHandle {

    private String method;
    private String path;
    private String accept;
    private String type;
    private HandlerConfig config;


    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getAccept() {
        return accept;
    }

    public String getType() {
        return type;
    }

    public HandlerConfig getConfig() {
        return config;
    }

    public ServletHolder handler(HandlerFunction handler){
        return new ServletHolder(new HandlerServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
                handler.apply(request, response, promise);
            }
        });
    }

    public void register(String method, String path, String accept, String type){
        register(method, path, accept, type, config -> config.setAsyncSupported(true));
    }

    public void register(String method, String path, String accept, String type, HandlerConfig config){
        this.method = method;
        this.path = path;
        this.accept = accept;
        this.type = type;
        this.config = config;
    }

    public abstract HandlerFunction start();
}
