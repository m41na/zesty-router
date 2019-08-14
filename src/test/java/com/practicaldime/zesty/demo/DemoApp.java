package com.practicaldime.zesty.demo;

import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.servlet.HandlerPromise;
import com.practicaldime.zesty.servlet.HandlerRequest;
import com.practicaldime.zesty.servlet.HandlerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DemoApp {

    private static final Logger LOG = LoggerFactory.getLogger(DemoApp.class);

    public static void main(String... args) {

        int port = 8080;
        String host = "localhost";

        Map<String, String> props = new HashMap<>();
        //props.put("appctx", "/app");
        //props.put("assets", "");
        //props.put("engine", "freemarker");

        AppServer app = new AppServer(props);

        app.router().get("/", (HandlerRequest request, HandlerResponse response, HandlerPromise promise) -> {
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }).thenAccept((Void) -> {
                response.send(String.format("incoming request: '%s'", request.getRequestURI()));
                promise.complete();
            });
        }).get("/a", (HandlerRequest request, HandlerResponse response, HandlerPromise promise) -> {
            promise.resolve(
                    CompletableFuture.runAsync(() -> {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace(System.err);
                        }
                    }).thenAccept((Void) -> {
                        response.send(String.format("incoming request: '%s'", request.getRequestURI()));
                    })
            );
        }).get("/b", (HandlerRequest request, HandlerResponse response, HandlerPromise promise) -> {
            response.render("demo", Collections.emptyMap());
            promise.complete();
        }).listen(port, host, (msg) -> LOG.info(msg));
    }
}
