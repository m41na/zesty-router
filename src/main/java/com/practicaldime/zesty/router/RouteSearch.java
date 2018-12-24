package com.practicaldime.zesty.router;

import java.util.HashMap;
import java.util.Map;

public class RouteSearch {

	public final Request request;
	public final Map<String, String> pathParams = new HashMap<>();
	public Route result;
	
	public RouteSearch(Request request) {
		super();
		this.request = request;
	}
	
	public void visit(Router router) {
		router.accept(this);
	}
}
