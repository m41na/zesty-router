package com.practicaldime.zesty.view.ejs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.ViewLookup;
import com.practicaldime.zesty.view.ViewProcessor;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class EJsViewEngine implements ViewEngine {

    private static EJsViewEngine instance;
    private final EJsViewConfiguration config;
    private final ViewProcessor<String, ViewLookup> view;
    private final String templateDir;
    private final String templateExt;
    private final ViewLookup strategy;

    private EJsViewEngine(String templateDir, String templateExt, String lookup) throws IOException {
        super();
        this.templateDir = templateDir;
        this.templateExt = templateExt;
        this.config = new EJsViewConfiguration();
        this.strategy = Arrays.asList(ViewLookup.values()).stream().filter(value -> value.toString().equalsIgnoreCase(lookup)).findFirst().orElse(ViewLookup.FILE);
        this.view = new EJsViewProcessor(config);
    }

    public static EJsViewEngine create(String templateDir, String templateExt, String lookup) {
        if (instance == null) {
            synchronized (EJsViewEngine.class) {
                try {
                    instance = new EJsViewEngine(templateDir, templateExt, lookup);
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    throw new RuntimeException("Could not create view engine", e);
                }
            }
        }
        return instance;
    }

    public static EJsViewEngine instance() throws IOException {
        return instance;
    }

    public static ViewProcessor getProcessor() {
        return EJsViewEngine.instance.view;
    }

    @Override
    public String templateDir() {
        return this.templateDir;
    }

    @Override
    public String templateExt() {
        return this.templateExt;
    }

    @Override
    public String merge(String template, Map<String, Object> model) throws Exception {
        String templateFile = view.resolve(templateDir, template, strategy);
        Value renderFunction = config.getEnvironment().getBindings("js").getMember("renderTemplate");
        Object result = renderFunction.execute(templateFile, new ObjectMapper().writeValueAsString(model), template);
        return result.toString();
    }
}
