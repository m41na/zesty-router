package com.practicaldime.zesty.router;

import java.util.HashMap;
import java.util.Map;

public class PathPartsRouter implements Routing.Router{

	private Map<Integer, Routing.Router> routers = new HashMap<>();

	@Override
	public void search(Routing.Search input) {
		String inputPath = input.attributes.url;
		String path = inputPath != null? (inputPath.startsWith("/")? inputPath.substring(1) : inputPath) : null;
		String[] parts = path != null? path.split("/") : null;
		if(parts != null) {
			Integer length = Integer.valueOf(parts.length);
			if(routers.containsKey(length)) {
				routers.get(length).search(input);
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
		String routePath = route.path;
		String path = routePath != null? (routePath.startsWith("/")? routePath.substring(1) : routePath) : null;
		String[] parts = path != null? path.split("/") : null;
		if(parts != null) {
			Integer length = Integer.valueOf(parts.length);
			if(!routers.containsKey(length)) {
				routers.put(length, new PathRegexRouter());
			}
			routers.get(length).add(route);
		}
	}

	@Override
	public void remove(Routing.Route entity) {

	}
}
