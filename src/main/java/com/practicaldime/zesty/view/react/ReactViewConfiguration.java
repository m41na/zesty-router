package com.practicaldime.zesty.view.react;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;

public class ReactViewConfiguration {

    private final ScriptEngine nashornEngine = new ScriptEngineManager().getEngineByName("nashorn");
    private final String POLYFILL = "/template/js/polyfill.js";
    private final String REACT_JS = "/template/js/react.production.min.js";
    private final String REACT_JS_DOM = "/template/js/react-dom.production.min.js";
    private final String EJS = "/template/js/ejs.min.js";
    private final String EJS_RENDER = "/template/js/ejs.render.js";

    public ReactViewConfiguration() throws ScriptException{
        nashornEngine.eval(new InputStreamReader(this.getClass().getResourceAsStream(POLYFILL)));
        nashornEngine.eval(new InputStreamReader(this.getClass().getResourceAsStream(EJS)));
        nashornEngine.eval(new InputStreamReader(this.getClass().getResourceAsStream(EJS_RENDER)));
        nashornEngine.eval(new InputStreamReader(this.getClass().getResourceAsStream(REACT_JS)));
        nashornEngine.eval(new InputStreamReader(this.getClass().getResourceAsStream(REACT_JS_DOM)));
    }

    public ScriptEngine getEnvironment()  {
        return nashornEngine;
    }
}
