package com.practicaldime.router.core.handler;

import com.practicaldime.router.core.routing.Routing;
import org.eclipse.jetty.server.Request;

public interface RouteMatcher {

    void addRoute(Routing.Route route);

    Routing.Search match(Request search);
}
