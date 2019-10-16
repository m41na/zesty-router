package com.practicaldime.zesty.router;

import java.util.EnumMap;
import java.util.Map;

public class MethodRouter implements Routing.Router{

	public enum Method {POST, GET, PUT, DELETE, OPTIONS, HEAD, ALL}

    private Map<Method, Routing.Router> routers = new EnumMap<>(Method.class);
	
	public MethodRouter() {
		super();
		routers.put(Method.GET, new PathPartsRouter());
		routers.put(Method.PUT, new PathPartsRouter());
		routers.put(Method.POST, new PathPartsRouter());
		routers.put(Method.HEAD, new PathPartsRouter());
		routers.put(Method.DELETE, new PathPartsRouter());
		routers.put(Method.OPTIONS, new PathPartsRouter());
		routers.put(Method.ALL, new PathPartsRouter());
	}

	@Override
	public void search(Routing.Search input) {
		String method = input.attributes.method;
		Method type = method != null? Method.valueOf(method.toUpperCase()) : null;
		if(type != null) {
			this.routers.get(type).search(input);
			//if no match if found for the specific request method, look into 'all' methods
			if(input.result == null) {
				this.routers.get(Method.ALL).search(input);
			}
			//if a matching route is found, set the method value in the result
			if(input.result != null) {
				input.result.method = type.name();
			}
		}
	}

	@Override
	public boolean contains(Routing.Search criteria) {
		return false;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public void add(Routing.Route route) {
		String method = route.method;
		Method type = method != null? Method.valueOf(method.toUpperCase()) : null;
		if(type != null) {
			this.routers.get(type).add(route);
		}
	}

	@Override
	public void remove(Routing.Route entity) {

	}
}
