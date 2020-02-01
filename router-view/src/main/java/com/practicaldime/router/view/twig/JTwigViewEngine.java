package com.practicaldime.router.view.twig;

import com.practicaldime.router.core.view.ViewEngine;
import com.practicaldime.router.core.view.ViewProcessor;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.resource.reference.ResourceReference;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class JTwigViewEngine implements ViewEngine {

    private static JTwigViewEngine instance;
    private final ViewConfiguration config;
    private final ViewProcessor<JtwigTemplate, ResourceReference> view;
    private final String templateDir;
    private final String templateExt;

    private JTwigViewEngine(String templateDir, String templateExt) {
        super();
        this.templateDir = templateDir;
        this.templateExt = templateExt;
        this.config = new JTwigViewConfiguration();
        this.view = new JTwigViewProcessor(config);
    }

    public static JTwigViewEngine create(String templateDir, String templateExt) throws IOException {
        if (instance == null) {
            synchronized (JTwigViewEngine.class) {
                instance = new JTwigViewEngine(templateDir, templateExt);
            }
        }
        return instance;
    }

    public static JTwigViewEngine instance() throws IOException {
        return instance;
    }

    public static ViewConfiguration getConfiguration() throws IOException {
        return instance().config;
    }

    public static ViewProcessor<JtwigTemplate, ResourceReference> getProcessor() throws IOException {
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
        String baseDir = System.getProperty("user.dir");
        Path path = Paths.get(baseDir, templateDir);
        JtwigTemplate resolved = view.resolve(path.resolve(template + "." + templateExt).toString(), "", ResourceReference.file(templateDir));
        return resolved.render(JtwigModel.newModel(model));
    }
}
