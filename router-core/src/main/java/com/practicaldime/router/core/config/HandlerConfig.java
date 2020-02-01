package com.practicaldime.router.core.config;

import org.eclipse.jetty.servlet.ServletHolder;

public interface HandlerConfig {

    void configure(ServletHolder holder);
}
