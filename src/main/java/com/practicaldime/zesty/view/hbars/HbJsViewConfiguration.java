package com.practicaldime.zesty.view.hbars;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;

public class HbJsViewConfiguration {

    private final ScriptEngine nashornEngine = new ScriptEngineManager().getEngineByName("nashorn");
    private final String POLYFILL = "/template/js/polyfill.js";
    private final String HANDLEBARS = "/template/js/handlebars-v4.1.2.js";
    private final String HANDLEBARS_HELPERS = "/template/js/handlebars.helpers.js";

    public HbJsViewConfiguration() throws ScriptException{
        nashornEngine.eval(new InputStreamReader(this.getClass().getResourceAsStream(POLYFILL)));
        nashornEngine.eval(new InputStreamReader(this.getClass().getResourceAsStream(HANDLEBARS)));
        nashornEngine.eval(new InputStreamReader(this.getClass().getResourceAsStream(HANDLEBARS_HELPERS)));
    }

    public ScriptEngine getEnvironment()  {
        return nashornEngine;
    }
}
