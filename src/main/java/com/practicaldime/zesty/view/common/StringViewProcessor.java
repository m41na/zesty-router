package com.practicaldime.zesty.view.common;

import com.practicaldime.zesty.view.ViewLookup;
import com.practicaldime.zesty.view.ViewProcessor;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StringViewProcessor implements ViewProcessor<String, ViewLookup> {

    @Override
    public String resolve(String templateDir, String template, ViewLookup strategy) throws Exception {
        switch (strategy) {
            case FILE: {
                String baseDir = System.getProperty("user.dir");
                Path path = Paths.get(baseDir, templateDir);
                if (Files.exists(path)) {
                    return new String(Files.readAllBytes(path.resolve(template)));
                }
                throw new RuntimeException("File '" + template + "' does not exist");
            }
            case CLASSPATH: {
                URL url = this.getClass().getClassLoader().getResource("");
                String baseDir = url.getPath();
                Path path = Paths.get(baseDir, templateDir, template);
                if (Files.exists(path)) {
                    return new String(Files.readAllBytes(path));
                }
                throw new RuntimeException("Resource '" + template + "' does not exist");
            }
            default: {
                return null;
            }
        }
    }
}
