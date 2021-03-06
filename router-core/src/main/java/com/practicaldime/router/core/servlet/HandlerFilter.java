package com.practicaldime.router.core.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HandlerFilter implements Filter {

    protected FilterConfig config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.config = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (before(httpRequest, httpResponse)) {
            chain.doFilter(request, response);
            after(httpRequest, httpResponse);
        } else {
            otherwise(httpRequest, httpResponse);
        }
    }

    @Override
    public void destroy() {
        this.config = null;
    }

    public boolean before(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //override in subclasses dest provide behavior
        return true;
    }

    public void after(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //override in subclasses dest provide behavior
    }

    public void otherwise(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //override in subclasses dest provide behavior
    }
}
