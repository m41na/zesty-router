package com.practicaldime.zesty.view.string;

import java.io.IOException;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.google.gson.Gson;
import com.practicaldime.zesty.view.ViewEngine;

public class StringViewEngine implements ViewEngine{

	private static StringViewEngine instance;
	private final Gson gson = new Gson();
    private final ViewProcessor view;
	private final String templateDir;
	private final String templateExt;
	
	private StringViewEngine(String templateDir, String templateExt) {
    	super();
        this.templateDir = templateDir;
        this.templateExt = templateExt;
        this.view = new StringViewProcessor();
	}
	
	@Override
	public String templateDir() {
		return this.templateDir;
	}

	@Override
	public String templateExt() {
		return this.templateExt;
	}
	
	public static StringViewEngine create(String templateDir, String templateExt) throws IOException {
        if (instance == null) {
            synchronized (StringViewEngine.class) {
                instance = new StringViewEngine(templateDir, templateExt);
            }
        }
        return instance;
    }
    
    public static StringViewEngine instance() throws IOException {
    	return instance;
    }
    
    public static ViewProcessor getProcessor() {
        return StringViewEngine.instance.view;
    }

	@Override
	public String merge(String template, Map<String, Object> model) throws Exception {		
		Context context = Context.newBuilder("js")
				.allowIO(true)
				.allowCreateThread(true)
				.allowHostAccess(true).build();
		
		Value bindings = context.getBindings("js");
		bindings.putMember("dist", templateDir);
		bindings.putMember("model", gson.toJson(model));
    	
    	String script = view.resolve(templateDir, template + "." + templateExt);
    	//server-side rendered content
    	Value result = context.eval(Source.newBuilder("js", script, template).build());
		
    	//page markup
    	String regex = model.get("view-regex").toString();
    	String markup = result.asString().replaceFirst(regex, result.asString());
    	return markup;
	}
}
