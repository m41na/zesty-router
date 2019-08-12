package com.practicaldime.zesty.view.react;

import com.practicaldime.zesty.view.ViewEngine;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.Map;

public class ReactViewEngine implements ViewEngine{

	private static ReactViewEngine instance;
	private final ReactViewConfiguration config;
    private final ViewProcessor<String> view;
	private final String templateDir;
	private final String templateExt;
	private final ViewProcessor.Lookup strategy = ViewProcessor.Lookup.FILE;
	private final String renderFunction = "ejsRender";

	private ReactViewEngine(String templateDir, String templateExt) throws ScriptException {
    	super();
        this.templateDir = templateDir;
        this.templateExt = templateExt;
        this.config = new ReactViewConfiguration();
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
	
	public static ReactViewEngine create(String templateDir, String templateExt)  {
        if (instance == null) {
            synchronized (ReactViewEngine.class) {
            	try {
					instance = new ReactViewEngine(templateDir, templateExt);
				}
            	catch(ScriptException e){
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
    	String templateFunction = view.resolve(templateDir, template, strategy);

		Object result = ((Invocable)config.getEnvironment()).invokeFunction(renderFunction, templateFunction, model);
		return result.toString();
	}
}
