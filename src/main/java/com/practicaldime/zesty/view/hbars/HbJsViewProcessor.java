package com.practicaldime.zesty.view.hbars;

import javax.script.Invocable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HbJsViewProcessor implements ViewProcessor<Object> {

    private final HbJsViewConfiguration factory;

    public HbJsViewProcessor(HbJsViewConfiguration factory) {
        this.factory = factory;
    }

    @Override
    public Object resolve(String templateDir, String template, Lookup strategy) throws Exception {
        //return a handlebars template
        String compileHandlebars = "function compileHandlebars(source) {return Handlebars.compile(source);}";
        switch (strategy) {
            case FILE: {
                String baseDir = System.getProperty("user.dir");
                Path path = Paths.get(baseDir, templateDir);
                String source = new String(Files.readAllBytes(path.resolve(template)));
                factory.getEnvironment().eval(compileHandlebars);
                return ((Invocable)factory.getEnvironment()).invokeFunction("compileHandlebars", source);
            }
            case CLASSPATH: {
                URL url = this.getClass().getClassLoader().getResource("");
                String baseDir = resolveName(url.getPath());
                Path path = Paths.get(baseDir, templateDir, template);
                String source = new String(Files.readAllBytes(path));
                factory.getEnvironment().eval(compileHandlebars);
                return ((Invocable)factory.getEnvironment()).invokeFunction("compileHandlebars", source);
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
