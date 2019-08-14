package com.practicaldime.zesty.view.hbars;

import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.ViewLookup;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class HbJsViewEngine implements ViewEngine{

	private static HbJsViewEngine instance;
	private final HbJsViewConfiguration config;
    private final ViewProcessor view;
	private final String templateDir;
	private final String templateExt;
	private final ViewLookup strategy;

	private HbJsViewEngine(String templateDir, String templateExt, String lookup) throws ScriptException {
    	super();
        this.templateDir = templateDir;
        this.templateExt = templateExt;
        this.config = new HbJsViewConfiguration();
		this.strategy = Arrays.asList(ViewLookup.values()).stream().filter(value -> value.toString().equalsIgnoreCase(lookup)).findFirst().orElse(ViewLookup.FILE);
        this.view = new HbJsViewProcessor(config);
	}
	
	@Override
	public String templateDir() {
		return this.templateDir;
	}

	@Override
	public String templateExt() {
		return this.templateExt;
	}
	
	public static HbJsViewEngine create(String templateDir, String templateExt, String lookup)  {
        if (instance == null) {
            synchronized (HbJsViewEngine.class) {
            	try {
					instance = new HbJsViewEngine(templateDir, templateExt, lookup);
				}
            	catch(ScriptException e){
            		e.printStackTrace(System.err);
            		throw new RuntimeException("Could not create view engine", e);
				}
            }
        }
        return instance;
    }
    
    public static HbJsViewEngine instance() throws IOException {
    	return instance;
    }
    
    public static ViewProcessor getProcessor() {
        return HbJsViewEngine.instance.view;
    }

    @Override
	public String merge(String template, Map<String, Object> model) throws Exception {	    	
    	Object templateFunction = view.resolve(templateDir, template, strategy);
		String mergeFunction = "function mergeFunction(template, context){return template(context);}";
		config.getEnvironment().eval(mergeFunction);
		Object result = ((Invocable)config.getEnvironment()).invokeFunction("mergeFunction", templateFunction, model);
		return result.toString();
	}
}
