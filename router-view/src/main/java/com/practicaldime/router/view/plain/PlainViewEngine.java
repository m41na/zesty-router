package com.practicaldime.router.view.plain;

import com.practicaldime.router.core.view.ViewEngine;
import com.practicaldime.router.core.view.ViewLookup;
import com.practicaldime.router.core.view.ViewProcessor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class PlainViewEngine implements ViewEngine {

    private static PlainViewEngine instance;
    private final ViewProcessor<String, ViewLookup> view;
    private final String templateDir;
    private final String templateExt = "*";
    private final ViewLookup strategy;

    private PlainViewEngine(String templateDir, String lookup) {
        super();
        this.templateDir = templateDir;
        this.strategy = Arrays.asList(ViewLookup.values()).stream().filter(value -> value.toString().equalsIgnoreCase(lookup)).findFirst().orElse(ViewLookup.FILE);
        this.view = new PlainViewProcessor();
    }

    public static PlainViewEngine create(String templateDir, String lookup) throws IOException {
        if (instance == null) {
            synchronized (PlainViewEngine.class) {
                instance = new PlainViewEngine(templateDir, lookup);
            }
        }
        return instance;
    }

    public static PlainViewEngine instance() throws IOException {
        return instance;
    }

    public static ViewProcessor getProcessor() throws IOException {
        return instance().view;
    }

    @Override
    public String templateDir() {
        return this.templateDir;
    }

    @Override
    public String templateExt() {
        return this.templateExt;
    }

    @Override
    public String merge(String template, Map<String, Object> model) throws Exception {
        return view.resolve(templateDir, template, strategy);
    }
}
