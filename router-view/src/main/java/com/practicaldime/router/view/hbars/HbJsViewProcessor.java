package com.practicaldime.router.view.hbars;

import com.practicaldime.router.view.common.StringViewProcessor;

public class HbJsViewProcessor extends StringViewProcessor {

    private final HbJsViewConfiguration factory;

    public HbJsViewProcessor(HbJsViewConfiguration factory) {
        this.factory = factory;
    }
}
