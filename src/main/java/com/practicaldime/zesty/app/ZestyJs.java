package com.practicaldime.zesty.app;

import java.io.FileReader;
import java.io.IOException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

public class ZestyJs {

	private static final NashornScriptEngineFactory FACTORY = new NashornScriptEngineFactory();
	public static final ScriptEngine ENGINE = FACTORY.getScriptEngine("-scripting", "-doe", "-ot", "--language=es6");

	static {
		String baseDir = System.getProperty("user.dir");

		ScriptContext context = ENGINE.getContext();
		context.setAttribute("zesty", new AppProvider(), ScriptContext.ENGINE_SCOPE);
		context.setAttribute("__dirname", baseDir, ScriptContext.ENGINE_SCOPE);
	}

	public void start(String... args) throws ScriptException, NoSuchMethodException, IOException {
		if (args != null && args.length > 0) {
			ENGINE.eval(new FileReader(args[0]));
		} else {
			ENGINE.eval(new FileReader("www/zestyjs.js"));
		}
	}

	public static void ping() throws ScriptException {
		ENGINE.eval("print('very much alive here');");
	}

	public static void main(String... args) throws ScriptException, NoSuchMethodException, IOException {
		new ZestyJs().start(args);
	}
}
