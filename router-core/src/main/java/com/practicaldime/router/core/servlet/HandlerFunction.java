package com.practicaldime.router.core.servlet;

public interface HandlerFunction {

    void apply(HandlerRequest request, HandlerResponse response, HandlerPromise promise);
}
