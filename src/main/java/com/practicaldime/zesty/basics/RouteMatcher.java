package com.practicaldime.zesty.basics;

import com.practicaldime.zesty.router.Route;
import com.practicaldime.zesty.router.RouteSearch;
import org.eclipse.jetty.server.Request;

public interface RouteMatcher {

	void addRoute(Route route);
	
	RouteSearch match(Request search);
}
