package com.practicaldime.zesty.router;

import java.util.EnumMap;
import java.util.Map;

public class MethodRouter implements Router{

	public static enum Method {POST, GET, PUT, DELETE};
	private Map<Method, Router> routers = new EnumMap<>(Method.class);
	
	public MethodRouter() {
		super();
		routers.put(Method.GET, new PathPartsRouter());
		routers.put(Method.PUT, new PathPartsRouter());
		routers.put(Method.POST, new PathPartsRouter());
		routers.put(Method.DELETE, new PathPartsRouter());
	}

	@Override
	public void accept(RouteSearch input) {
		String method = input.request.method;
		Method type = method != null? Method.valueOf(method.toUpperCase()) : null;
		if(type != null) {
			this.routers.get(type).accept(input);
			//if a matching route is found, set the method value in the result
			if(input.request != null) {
				input.result.method = type.name();
			}
		}
	}

	@Override
	public void addRoute(Route route) {
		String method = route.method;
		Method type = method != null? Method.valueOf(method.toUpperCase()) : null;
		if(type != null) {
			this.routers.get(type).addRoute(route);
		}
	}
}
