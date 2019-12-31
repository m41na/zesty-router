package com.practicaldime.zesty.demo;

import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.servlet.HandlerPromise;
import com.practicaldime.zesty.servlet.HandlerRequest;
import com.practicaldime.zesty.servlet.HandlerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DemoApp {

    private static final Logger LOG = LoggerFactory.getLogger(DemoApp.class);

    public static void main(String... args) {
        //to run multiple processes, execute from command line and pass different port numbers
        //$JAVA_HOME/bin/java -cp "target\zesty-router-0.1.1-shaded.jar;target\test-classes" com.practicaldime.zesty.demo.DemoApp --port 8090 --host localhost
        int port = 8080;//Simproxy.freePort();
        String host = "localhost";

        //evaluate params to override defaults
        List<String> params = Arrays.asList(args);
        for (int i = 0; i < params.size(); i++) {
            switch (params.get(i)) {
                case "--port": {
                    port = Integer.parseInt(params.get(++i));
                    break;
                }
                case "--host": {
                    host = params.get(++i);
                    break;
                }
            }
        }

        start(port, host);
    }

    public static void start(int port, String host) {
        AppServer.instance().templates("target/classes/").get("/", (HandlerRequest request, HandlerResponse response, HandlerPromise promise) -> {
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }).thenAccept((Void) -> {
                response.send(String.format("incoming request: '%s on port %d'", request.getRequestURI(), request.getRequest().getLocalPort()));
                promise.complete();
            });
        }).get("/a", (HandlerRequest request, HandlerResponse response, HandlerPromise promise) -> {
            promise.resolve(
                    CompletableFuture.runAsync(() -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace(System.err);
                        }
                    }).thenAccept((Void) -> {
                        response.send(String.format("incoming request: '%s on port %d'", request.getRequestURI(), request.getRequest().getLocalPort()));
                    })
            );
        }).get("/b", (HandlerRequest request, HandlerResponse response, HandlerPromise promise) -> {
            response.render("config/application.default.json", null);
            promise.complete();
        }).listen(port, host, (msg) -> LOG.info(msg));
    }
}
