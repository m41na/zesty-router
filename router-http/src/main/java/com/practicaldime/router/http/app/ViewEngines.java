package com.practicaldime.router.http.app;

import com.practicaldime.router.core.view.ViewEngine;
import com.practicaldime.router.core.view.ViewEngineFactory;
import com.practicaldime.router.view.ejs.EJsViewEngine;
import com.practicaldime.router.view.ftl.FtlViewEngine;
import com.practicaldime.router.view.hbars.HbJsViewEngine;
import com.practicaldime.router.view.plain.PlainViewEngine;
import com.practicaldime.router.view.react.ReactViewEngine;
import com.practicaldime.router.view.twig.JTwigViewEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewEngines implements ViewEngineFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ViewEngines.class);
    private final Map<String, ViewEngine> engines = new HashMap<>();

    @Override
    public ViewEngine engine(String view, String assets, String suffix, String lookup) {
        try {
            switch (view) {
                case "jtwig":
                    if (engines.get(view) == null) {
                        engines.put(view, JTwigViewEngine.create(assets, suffix));
                    }
                    return engines.get(view);
                case "freemarker":
                    if (engines.get(view) == null) {
                        engines.put(view, FtlViewEngine.create(assets, suffix));
                    }
                    return engines.get(view);
                case "handlebars":
                    if (engines.get(view) == null) {
                        engines.put(view, HbJsViewEngine.create(assets, suffix, lookup));
                    }
                    return engines.get(view);
                case "com.practicaldime.router.view.ejs":
                    if (engines.get(view) == null) {
                        engines.put(view, EJsViewEngine.create(assets, suffix, lookup));
                    }
                    return engines.get(view);
                case "com.practicaldime.router.view.react":
                    if (engines.get(view) == null) {
                        engines.put(view, ReactViewEngine.create(assets, suffix, lookup));
                    }
                    return engines.get(view);
                default:
                    LOG.warn("specified engine not supported. defaulting to 'plain' instead");
                    if (engines.get(view) == null) {
                        engines.put(view, PlainViewEngine.create(assets, lookup));
                    }
                    return engines.get(view);
            }
        } catch (IOException e) {
            throw new RuntimeException("problem setting up view engine", e);
        }
    }
}
