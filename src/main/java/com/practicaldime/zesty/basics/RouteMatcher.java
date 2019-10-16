package com.practicaldime.zesty.basics;

import com.practicaldime.zesty.router.Routing;
import com.practicaldime.zesty.router.Routing.Search;
import org.eclipse.jetty.server.Request;

public interface RouteMatcher {

    void addRoute(Routing.Route route);

    Search match(Request search);
}
