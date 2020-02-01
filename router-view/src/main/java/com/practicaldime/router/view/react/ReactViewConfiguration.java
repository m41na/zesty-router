package com.practicaldime.router.view.react;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.io.InputStreamReader;

public class ReactViewConfiguration {

    private final Context context = Context.newBuilder("js")
            .allowAllAccess(true)
            .allowExperimentalOptions(true).build();
    private final String POLYFILL = "/template/js/polyfill.js";
    private final String REACT_JS = "/template/js/react.production.min.js";
    private final String REACT_DOM_JS = "/template/js/react-dom.production.min.js";

    public ReactViewConfiguration() throws IOException {
        context.eval(Source.newBuilder("js", new InputStreamReader(this.getClass().getResourceAsStream(POLYFILL)), POLYFILL).build());
        context.eval(Source.newBuilder("js", new InputStreamReader(this.getClass().getResourceAsStream(REACT_JS)), REACT_JS).build());
        context.eval(Source.newBuilder("js", new InputStreamReader(this.getClass().getResourceAsStream(REACT_DOM_JS)), REACT_DOM_JS).build());
    }

    public Context getEnvironment() {
        return context;
    }
}
