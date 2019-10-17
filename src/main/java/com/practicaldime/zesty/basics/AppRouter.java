package com.practicaldime.zesty.basics;

import com.practicaldime.zesty.router.Routing;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

public class AppRouter implements Routing.Router {

    private final Routing.Router routeTree;

    public AppRouter(Routing.Router routeTree) {
        super();
        this.routeTree = routeTree;
    }

    public Routing.Search search(HttpServletRequest request) {
        Routing.Attributes search = new Routing.Attributes();
        search.url = request.getRequestURI();
        search.method = request.getMethod();
        for (Enumeration<String> keys = request.getHeaderNames(); keys.hasMoreElements(); ) {
            String key = keys.nextElement();
            search.headers.put(key, request.getHeader(key));
        }
        return searchRoute(search);
    }

    public Routing.Search searchRoute(Routing.Attributes requestAttrs) {
        Routing.Search input = new Routing.Search(requestAttrs);
        this.search(input);
        return input;
    }

    @Override
    public void search(Routing.Search input) {
        routeTree.search(input);
    }

    @Override
    public boolean contains(Routing.Search criteria) {
        return routeTree.contains(criteria);
    }

    @Override
    public void info(List<String> nodes, String prefix) {
        String indent = prefix + "|-";
        nodes.add(indent + "root");
        routeTree.info(nodes, indent);
    }

    @Override
    public void add(Routing.Route route) {
        routeTree.add(route);
    }

    @Override
    public void remove(Routing.Route entity) {
        routeTree.remove(entity);
    }
}
