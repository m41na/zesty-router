package com.practicaldime.zesty.demo;

import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.servlet.HandlerConfig;
import com.practicaldime.zesty.servlet.HandlerPromise;
import com.practicaldime.zesty.servlet.HandlerRequest;
import com.practicaldime.zesty.servlet.HandlerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.PushBuilder;

public class H2PushApp {

    private static final Logger LOG = LoggerFactory.getLogger(H2PushApp.class);

    public static void main(String... args) {
        int port = 8088;
        String host = "localhost";

        start(port, host);
    }

    public static void start(int port, String host) {git
        new AppServer().router().assets("/","/src/test/resources/webapp/page/assets/").templates("/src/test/resources/webapp/page/")
                .get("/", cfg -> cfg.setAsyncSupported(true), (HandlerRequest request, HandlerResponse response, HandlerPromise promise) -> {
                    HttpServletRequest req = request;
                    PushBuilder pushBuilder = req.newPushBuilder();
                    pushBuilder
                            .path("index.css")
                            .addHeader("content-type", "text/css")
                            .path("index.js")
                            .addHeader("content-type", "text/javascript")
                            .push();
                    response.render("index.html", null);
                    promise.complete();
                }).listen(port, host, (msg) -> LOG.info(msg));
    }
}
