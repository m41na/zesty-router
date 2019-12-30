package com.practicaldime.zesty.app;

import java.util.Map;

public interface AppProvider {

    default AppServer provide(Map<String, String> props) {
        return new AppServer(props);
    }
}
