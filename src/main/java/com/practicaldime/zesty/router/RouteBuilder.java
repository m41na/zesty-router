package com.practicaldime.zesty.router;

import java.util.Collections;
import java.util.Map;

public class RouteBuilder {

	public static Route create(String rid, String path, String method, String accepts, String contentType, Map<String, String> headers) {
		Route route = new Route();
		route.rid = rid;
		route.path = path;
		route.method = method;
		route.accepts = accepts;
		route.contentType = contentType;
		route.headers = headers;
		return route;
	}
	
	public static Route create(String rid, String path, String method, String accepts, String contentType) {
		return create(rid, path, method, accepts, contentType, Collections.emptyMap());
	}
	
	public static Route create(String rid, String path, String method, String accepts) {
		return create(rid, path, method, accepts, null, Collections.emptyMap());
	}
	
	public static Route create(String rid, String path, String method) {
		return create(rid, path, method, null, null, Collections.emptyMap());
	}
}
