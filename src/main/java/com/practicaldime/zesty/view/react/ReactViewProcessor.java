package com.practicaldime.zesty.view.react;

import com.practicaldime.zesty.view.ViewLookup;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReactViewProcessor implements ViewProcessor<String> {

    private final ReactViewConfiguration factory;

    public ReactViewProcessor(ReactViewConfiguration factory) {
        this.factory = factory;
    }

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
                String baseDir = resolveName(url.getPath());
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

    private String resolveName(String name) {
        if (name == null) {
            return name;
        }
        if (!name.startsWith("/")) {
            Class<?> c = this.getClass();
            while (c.isArray()) {
                c = c.getComponentType();
            }
            String baseName = c.getName();
            int index = baseName.lastIndexOf('.');
            if (index != -1) {
                name = baseName.substring(0, index).replace('.', '/') + "/" + name;
            }
        } else {
            name = name.substring(1);
        }
        return name;
    }
}
