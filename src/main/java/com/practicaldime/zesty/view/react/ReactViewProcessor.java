package com.practicaldime.zesty.view.react;

import com.practicaldime.zesty.view.common.StringViewProcessor;

public class ReactViewProcessor extends StringViewProcessor {

    private final ReactViewConfiguration factory;

    public ReactViewProcessor(ReactViewConfiguration factory) {
        this.factory = factory;
    }
}
