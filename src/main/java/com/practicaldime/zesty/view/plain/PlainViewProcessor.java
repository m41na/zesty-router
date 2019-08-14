package com.practicaldime.zesty.view.plain;

import com.practicaldime.zesty.view.ViewLookup;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlainViewProcessor implements ViewProcessor<String> {

    @Override
    public String resolve(String templateDir, String template, ViewLookup strategy) throws Exception {
        switch (strategy) {
            case FILE: {
                String baseDir = System.getProperty("user.dir");
                Path path = Paths.get(baseDir, templateDir);
                return (Files.exists(path)) ?
                        new String(Files.readAllBytes(path.resolve(template))) :
                        "File '" + template + "' does not exist";
            }
            case CLASSPATH: {
                URL url = this.getClass().getClassLoader().getResource("");
                String baseDir = resolveName(url.getPath());
                Path path = Paths.get(baseDir, templateDir, template);
                return (Files.exists(path)) ?
                        new String(Files.readAllBytes(path)) :
                        "Resource '" + template + "' does not exist";
            }
            case NONE: {
                return template;
            }
            default: {
                return "not yet implemented";
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
