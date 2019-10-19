package com.practicaldime.zesty.view.ftl;

import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.ViewProcessor;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FtlViewEngine implements ViewEngine {

    private static FtlViewEngine instance;
    private final ViewConfiguration config;
    private final ViewProcessor<Template, Configuration> view;
    private final String templateDir;
    private final String templateExt;

    private FtlViewEngine(String templateDir, String templateExt) throws IOException {
        super();
        this.templateDir = templateDir;
        this.templateExt = templateExt;
        this.config = new FtlViewConfiguration(templateDir);
        this.view = new FtlViewProcessor();
    }

    public static FtlViewEngine create(String templateDir, String templateExt) throws IOException {
        if (instance == null) {
            synchronized (FtlViewEngine.class) {
                instance = new FtlViewEngine(templateDir, templateExt);
            }
        }
        return instance;
    }

    public static FtlViewEngine instance() throws IOException {
        return instance;
    }

    public static ViewConfiguration getConfiguration() throws IOException {
        return instance().config;
    }

    public static ViewProcessor<Template, Configuration> getProcessor() throws IOException {
        return instance().view;
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
        StringWriter output = new StringWriter();
        Template resolved = view.resolve(template + "." + templateExt, "", config.getEnvironment());
        resolved.process(model, output);
        return output.toString();
    }
}
