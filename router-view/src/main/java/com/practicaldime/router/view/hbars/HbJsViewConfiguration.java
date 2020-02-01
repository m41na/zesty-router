package com.practicaldime.router.view.hbars;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.io.InputStreamReader;

public class HbJsViewConfiguration {

    private final Context context = Context.newBuilder("js")
            .allowAllAccess(true)
            .allowExperimentalOptions(true).build();
    private final String POLYFILL = "/template/js/polyfill.js";
    private final String HANDLEBARS = "/template/js/handlebars-v4.1.2.js";
    private final String HANDLEBARS_HELPERS = "/template/js/handlebars.helpers.js";
    private final String HANDLEBARS_RENDER = "/template/js/handlebars.render.js";

    public HbJsViewConfiguration() throws IOException {
        context.eval(Source.newBuilder("js", new InputStreamReader(this.getClass().getResourceAsStream(POLYFILL)), POLYFILL).build());
        context.eval(Source.newBuilder("js", new InputStreamReader(this.getClass().getResourceAsStream(HANDLEBARS)), HANDLEBARS).build());
        context.eval(Source.newBuilder("js", new InputStreamReader(this.getClass().getResourceAsStream(HANDLEBARS_HELPERS)), HANDLEBARS_HELPERS).build());
        context.eval(Source.newBuilder("js", new InputStreamReader(this.getClass().getResourceAsStream(HANDLEBARS_RENDER)), HANDLEBARS_RENDER).build());
    }

    public Context getEnvironment() {
        return context;
    }
}
