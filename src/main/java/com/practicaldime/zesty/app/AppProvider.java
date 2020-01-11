package com.practicaldime.zesty.app;

import org.apache.commons.cli.Options;

import java.util.Map;

public interface AppProvider {

    IServer provide(Map<String, String> properties);

    default void start(String[] args) {
        start(new Options(), args);
    }

    default void start(Options options, String[] args) {
        Map<String, String> properties = apply(AppOptions.applyDefaults(options, args));
        provide(properties).listen(Integer.parseInt(properties.get("port")), properties.get("host"), (result) -> System.out.println(result));
    }

    default Map<String, String> apply(Map<String, String> props){
        return props;
    }
}
