package com.practicaldime.zesty.view.ejs;

import com.practicaldime.zesty.view.common.StringViewProcessor;

public class EJsViewProcessor extends StringViewProcessor {

    private final EJsViewConfiguration factory;

    public EJsViewProcessor(EJsViewConfiguration factory) {
        this.factory = factory;
    }
}
