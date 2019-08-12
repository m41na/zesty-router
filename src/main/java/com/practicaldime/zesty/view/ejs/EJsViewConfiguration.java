package com.practicaldime.zesty.view.ejs;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.io.InputStreamReader;

public class EJsViewConfiguration {

    private final Context context = Context.newBuilder("js")
            .allowAllAccess(true)
            .allowExperimentalOptions(true).build();
    private final String POLYFILL = "/template/js/polyfill.js";
    private final String EJS = "/template/js/ejs.min.js";
    private final String EJS_RENDER = "/template/js/ejs.render.js";

    public EJsViewConfiguration() throws IOException {
        context.eval(Source.newBuilder("js", new InputStreamReader(this.getClass().getResourceAsStream(POLYFILL)), POLYFILL).build());
        context.eval(Source.newBuilder("js", new InputStreamReader(this.getClass().getResourceAsStream(EJS)), EJS).build());
        context.eval(Source.newBuilder("js", new InputStreamReader(this.getClass().getResourceAsStream(EJS_RENDER)), EJS_RENDER).build());
    }

    public Context getEnvironment()  {
        return context;
    }
}
