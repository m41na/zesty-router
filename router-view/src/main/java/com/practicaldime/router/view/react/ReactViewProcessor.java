package com.practicaldime.router.view.react;

import com.practicaldime.router.view.common.StringViewProcessor;

public class ReactViewProcessor extends StringViewProcessor {

    private final ReactViewConfiguration factory;

    public ReactViewProcessor(ReactViewConfiguration factory) {
        this.factory = factory;
    }
}
