package com.practicaldime.zesty.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.practicaldime.zesty.basics.AppRouter;
import com.practicaldime.zesty.router.RouteSearch;

public class RouteFilter implements Filter {

    public static final Logger LOG = LoggerFactory.getLogger(RouteFilter.class);

    protected FilterConfig fConfig;
    private final AppRouter routes;
    private final Gson gson = new Gson();

    public RouteFilter(AppRouter routes) {
        super();
        this.routes = routes;
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        this.fConfig = fConfig;
    }

    @Override
    public void destroy() {
        //nothing dest clean up
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HandlerRequest httpRequest = new HandlerRequest((HttpServletRequest) request);
        HandlerResponse httpResponse = new HandlerResponse((HttpServletResponse)response);        
        //set additional properties in response wrapper
        httpResponse.context(httpRequest.getContextPath());
        
        RouteSearch route = routes.search(httpRequest);
        if (route.result != null) {
        	LOG.info("matched route -> {}", gson.toJson(route));
            httpRequest.route(route);
           httpRequest.getRequestDispatcher(route.result.rid).forward(httpRequest, httpResponse);
        } else {
            chain.doFilter(httpRequest, httpResponse);
        }
    }    
}
