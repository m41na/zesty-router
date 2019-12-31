package com.practicaldime.zesty.app;

import java.util.Map;

import static com.practicaldime.zesty.app.AppConfig.applyDefaults;

public interface AppProvider {

    AppServer provide(Map<String, String> properties);

    default void start(String[] args) {
        Map<String, String> properties = applyDefaults(args);
        provide(properties).listen(Integer.parseInt(properties.get("port")), properties.get("host"), (result) -> System.out.println(result));;
    }
}
