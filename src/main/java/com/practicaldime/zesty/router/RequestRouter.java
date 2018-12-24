package com.practicaldime.zesty.router;

public class RequestRouter implements Router{

	private final Router routeTree;
	
	public RequestRouter(Router routeTree) {
		super();
		this.routeTree = routeTree;
	}
	
	public RouteSearch searchRoute(Request request) {
		RouteSearch input = new RouteSearch(request);
		this.accept(input);
		return input;
	}

	@Override
	public void accept(RouteSearch input) {
		routeTree.accept(input);
	}

	@Override
	public void addRoute(Route route) {
		routeTree.addRoute(route);
	}
}
