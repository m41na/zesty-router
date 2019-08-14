package com.practicaldime.zesty.view.react;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.ViewLookup;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class ReactViewEngine implements ViewEngine {

    private static ReactViewEngine instance;
    private final ReactViewConfiguration config;
    private final ViewProcessor<String> view;
    private final String templateDir;
    private final String templateExt;
    private final ViewLookup strategy;

    private ReactViewEngine(String templateDir, String templateExt, String lookup) throws IOException {
        super();
        this.templateDir = templateDir;
        this.templateExt = templateExt;
        this.config = new ReactViewConfiguration();
        this.strategy = Arrays.asList(ViewLookup.values()).stream().filter(value -> value.toString().equalsIgnoreCase(lookup)).findFirst().orElse(ViewLookup.FILE);
        this.view = new ReactViewProcessor(config);
    }

	@Override
	public String templateDir() {
		return this.templateDir;
	}

	@Override
	public String templateExt() {
		return this.templateExt;
	}

    public static ReactViewEngine create(String templateDir, String templateExt, String lookup) {
        if (instance == null) {
            synchronized (ReactViewEngine.class) {
                try {
                    instance = new ReactViewEngine(templateDir, templateExt, lookup);
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    throw new RuntimeException("Could not create React view engine", e);
                }
            }
        }
        return instance;
    }

    public static ReactViewEngine instance() throws IOException {
        return instance;
    }

    public static ViewProcessor getProcessor() {
        return ReactViewEngine.instance.view;
    }

    @Override
    public String merge(String template, Map<String, Object> model) throws Exception {
        String jsxAppBundle = view.resolve(templateDir, template, strategy);
        Value component = config.getEnvironment().eval(Source.newBuilder("js", jsxAppBundle, "app").build());
        Value reactRender = config.getEnvironment().getBindings("js").getMember("reactRender");
        Object result = reactRender.execute(component, new ObjectMapper().writeValueAsString(model));
        return result.toString();
    }
}
