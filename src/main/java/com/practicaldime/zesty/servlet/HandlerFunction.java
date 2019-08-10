package com.practicaldime.zesty.servlet;

public interface HandlerFunction {

    void apply(HandlerRequest request, HandlerResponse response, HandlerPromise promise);
}
