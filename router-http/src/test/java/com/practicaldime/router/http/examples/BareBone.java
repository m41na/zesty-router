package com.practicaldime.router.http.examples;

import com.practicaldime.router.core.server.IServer;
import com.practicaldime.router.core.server.Rest;
import com.practicaldime.router.http.app.AppServer;

import java.util.Map;
import java.util.function.Function;

public class BareBone {

    public static void main(String[] args) {
        Rest rest = new Rest() {

            @Override
            public IServer provide(Map<String, String> properties) {
                properties.put("port", "7890");
                return AppServer.instance(properties);
            }

            @Override
            public Function<Map<String, String>, IServer> build(IServer app) {
                return props ->
                    app.get("/", (handler) -> handler.setAsyncSupported(true), (req, res, done) -> {
                        res.send("hello world!!");
                        done.complete();
                    });
            }
        };

        rest.start(args);
    }
}
