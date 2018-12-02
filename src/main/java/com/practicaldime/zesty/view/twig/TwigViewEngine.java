package com.practicaldime.zesty.view.twig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.resource.reference.ResourceReference;

import com.practicaldime.zesty.view.ViewEngine;

public class TwigViewEngine implements ViewEngine{

	private static TwigViewEngine instance;
    private final ViewConfiguration config;
    private final ViewProcessor view;
    private final String templateDir;
	private final String templateExt;	

    private TwigViewEngine(String templateDir, String templateExt) {
    	super();
        this.templateDir = templateDir;
        this.templateExt = templateExt;
        this.config = new TwigViewConfiguration();
        this.view = new TwigViewProcessor(config);
    }
    
    public static TwigViewEngine create(String templateDir, String templateExt) throws IOException {
        if (instance == null) {
            synchronized (TwigViewEngine.class) {
                instance = new TwigViewEngine(templateDir, templateExt);
            }
        }
        return instance;
    }
    
    public static TwigViewEngine instance() throws IOException {
        return instance;
    }

    @Override
	public String templateDir() {
		return this.templateDir;
	}

	@Override
	public String templateExt() {
		return this.templateExt;
	}

	public static ViewConfiguration getConfiguration() {
        return TwigViewEngine.instance.config;
    }

    public static ViewProcessor getProcessor() {
        return TwigViewEngine.instance.view;
    }

    @Override
    public String merge(String template, Map<String, Object> model) throws Exception{
    	String baseDir = System.getProperty("user.dir");
    	Path path = Paths.get(baseDir, templateDir);
        JtwigTemplate resolved = getProcessor().resolve(path.resolve(template + "." + templateExt).toString(), ResourceReference.file(templateDir));
        return resolved.render(JtwigModel.newModel(model));
    }
}
