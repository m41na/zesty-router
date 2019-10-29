package com.practicaldime.zesty.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.zesty.basics.AppRouter;
import com.practicaldime.zesty.router.Routing.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RouteFilter implements Filter {

    public static final Logger LOG = LoggerFactory.getLogger(RouteFilter.class);
    private final AppRouter routes;
    private final ObjectMapper mapper;
    protected FilterConfig fConfig;

    public RouteFilter(AppRouter routes, ObjectMapper mapper) {
        super();
        this.routes = routes;
        this.mapper = mapper;
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
        HandlerResponse httpResponse = new HandlerResponse((HttpServletResponse) response);
        //set additional properties in response wrapper
        httpResponse.context(httpRequest.getContextPath());

        Search route = routes.search(httpRequest);
        if (route.result != null) {
            LOG.info("matched route -> {}", mapper.writeValueAsString(route));
            httpRequest.route(route);
            httpRequest.getRequestDispatcher(route.result.rid).forward(httpRequest, httpResponse);
        } else {
            chain.doFilter(httpRequest, httpResponse);
        }
    }
}
