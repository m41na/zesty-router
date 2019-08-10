package com.practicaldime.zesty.demo;

import java.util.HashMap;
import java.util.Map;

import com.practicaldime.zesty.servlet.HandlerPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.servlet.HandlerRequest;
import com.practicaldime.zesty.servlet.HandlerResponse;

public class DemoApp {

    private static final Logger LOG = LoggerFactory.getLogger(DemoApp.class);

    public static void main(String... args) {

        int port = 8080;
        String host = "localhost";

        Map<String, String> props = new HashMap<>();
        props.put("appctx", "/app");
        props.put("assets", "");
        props.put("engine", "freemarker");

        AppServer app = new AppServer(props);

        app.router().get("/", (HandlerRequest request, HandlerResponse response, HandlerPromise promise) -> {
            response.send(String.format("incoming request: '%s'", request.getRequestURI()));
            promise.complete();
        }).listen(port, host, (msg) -> LOG.info(msg));
    }
}
