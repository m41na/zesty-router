package com.practicaldime.router.core.server;

import org.apache.commons.cli.Options;

import java.util.Map;

import static com.practicaldime.router.core.server.AppOptions.applyDefaults;

public interface AppProvider {

    IServer provide(Map<String, String> properties);

    default void start(String[] args) {
        start(new Options(), args);
    }

    default void start(Options options, String[] args) {
        Map<String, String> properties = applyDefaults(options, args);
        provide(properties).listen(Integer.parseInt(properties.get("port")), properties.get("host"), (result) -> System.out.println(result));
    }
}
