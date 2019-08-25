package com.practicaldime.zesty.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.zesty.proxy.ProxyApp;
import com.practicaldime.zesty.proxy.ProxyConfig;

import java.io.File;
import java.io.FileInputStream;

public class QuickStart {

    public static void main(String... args) throws Exception {
        //to run multiple processes, execute from command line and pass different port numbers
        //$JAVA_HOME/bin/java -cp target/zesty-router-0.1.1-shaded.jar com.practicaldime.zesty.app.QuickStart --config app.member0.json
        //$JAVA_HOME/bin/java -jar target/zesty-router-0.1.1-shaded.jar --config app.member1.json

        ProxyConfig config = null;
        if (args.length > 0) {
            String configFile = args[1];
            if (configFile != null && configFile.trim().length() > 0) {
                File file = new File(configFile);
                if (file.exists()) {
                    config = new ObjectMapper().readValue(new FileInputStream(file), ProxyConfig.class);
                }
            }
        }

        //start proxy
        ProxyApp.start(config);
    }
}
