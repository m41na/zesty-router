package com.practicaldime.zesty.servlet;

import org.eclipse.jetty.servlet.ServletHolder;

public interface HandlerConfig {

    void configure(ServletHolder holder);
}
