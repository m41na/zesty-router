package com.practicaldime.router.core.view;

@FunctionalInterface
public interface ViewEngineFactory {

    ViewEngine engine(String view, String assets, String suffix, String lookup);
}
