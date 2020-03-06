package com.practicaldime.router.core.server;

import org.apache.commons.cli.Options;

import java.util.Map;
import java.util.function.Function;

import static com.practicaldime.router.core.server.AppOptions.applyDefaults;

public interface Rest {

    IServer provide(Map<String, String> properties);

    Function<IServer, IServer> compose();

    default void start(String[] args) {
        start(new Options(), args);
    }

    default void start(Options options, String[] args) {
        Map<String, String> properties = applyDefaults(options, args);
        compose().apply(provide(properties)).listen(Integer.parseInt(properties.get("port")), properties.get("host"), (result) -> System.out.println(result));
    }
}
