package com.practicaldime.zesty.view.hbars;

import com.practicaldime.zesty.view.common.StringViewProcessor;

public class HbJsViewProcessor extends StringViewProcessor {

    private final HbJsViewConfiguration factory;

    public HbJsViewProcessor(HbJsViewConfiguration factory) {
        this.factory = factory;
    }
}
