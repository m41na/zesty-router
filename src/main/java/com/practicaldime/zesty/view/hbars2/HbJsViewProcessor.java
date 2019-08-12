package com.practicaldime.zesty.view.hbars2;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HbJsViewProcessor implements ViewProcessor<String> {

    private final HbJsViewConfiguration factory;

    public HbJsViewProcessor(HbJsViewConfiguration factory) {
        this.factory = factory;
    }

    @Override
    public String resolve(String templateDir, String template, Lookup strategy) throws Exception {
        //return a handlebars template
        switch (strategy) {
            case FILE: {
                String baseDir = System.getProperty("user.dir");
                Path path = Paths.get(baseDir, templateDir);
                return new String(Files.readAllBytes(path.resolve(template)));
            }
            case CLASSPATH: {
                URL url = this.getClass().getClassLoader().getResource("");
                String baseDir = resolveName(url.getPath());
                Path path = Paths.get(baseDir, templateDir, template);
                return new String(Files.readAllBytes(path));
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
