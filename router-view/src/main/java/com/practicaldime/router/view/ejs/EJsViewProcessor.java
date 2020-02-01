package com.practicaldime.router.view.ejs;

import com.practicaldime.router.view.common.StringViewProcessor;

public class EJsViewProcessor extends StringViewProcessor {

    private final EJsViewConfiguration factory;

    public EJsViewProcessor(EJsViewConfiguration factory) {
        this.factory = factory;
    }
}
