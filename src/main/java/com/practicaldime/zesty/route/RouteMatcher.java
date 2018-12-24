package com.practicaldime.zesty.route;

import org.eclipse.jetty.server.Request;

public interface RouteMatcher {

	void addRoute(AppRoute route);
	
	AppRoute match(Request search);
}
