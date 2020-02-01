package com.practicaldime.router.http.demo;

import com.practicaldime.router.core.handler.RouteHandle;
import com.practicaldime.router.core.server.IServer;
import com.practicaldime.router.core.servlet.HandlerFunction;
import com.practicaldime.router.http.app.AppServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteHandleDemo {

    private static final Logger LOG = LoggerFactory.getLogger(DemoApp.class);

    public static void main(String... args) {
        int port = 8080;
        String host = "localhost";
        IServer app = AppServer.instance().templates("target/classes/");
        app.listen(port, host, res -> {
            LOG.info("server started. Now adding handler");
            app.accept(new RouteHandleDemo().helloHandle());
        });
    }

    public RouteHandle helloHandle() {
        class HelloHandle extends RouteHandle {

            public HelloHandle() {
                register("get", "/", "", "");
            }

            @Override
            public HandlerFunction start() {
                return (req, res, done) -> {
                    res.send("hello world!!");
                    done.complete();
                };
            }
        }
        return new HelloHandle();
    }
}
