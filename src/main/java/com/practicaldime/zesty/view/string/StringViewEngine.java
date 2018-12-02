package com.practicaldime.zesty.view.string;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;

import com.google.gson.Gson;
import com.practicaldime.zesty.app.ZestyJs;
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
		String baseDir = System.getProperty("user.dir");
		Path path = Paths.get(baseDir, templateDir);
		
		Bindings bindings = ZestyJs.ENGINE.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("dist", path.toString());		
    	
    	String script = new String(Files.readAllBytes(path.resolve(template + "." + templateExt)));
    	//server-side rendered content
		ZestyJs.ENGINE.eval(script);
		Invocable invocable = (Invocable) ZestyJs.ENGINE;

		Object result = invocable.invokeFunction("renderString", gson.toJson(model.get("tasks")));
		
    	//page markup
    	String markup = new String(Files.readAllBytes(path.resolve(template + ".html")));
    	String regex = "<div id=\"app\"></div>";
    	markup = markup.replaceFirst(regex, result.toString());
		return markup;
	}
}
