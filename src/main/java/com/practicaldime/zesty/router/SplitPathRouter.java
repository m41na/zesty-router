package com.practicaldime.zesty.router;

import java.util.HashMap;
import java.util.Map;

public class SplitPathRouter implements Routing.Router{

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
		String inputPath = criteria.attributes.url;
		String path = inputPath != null? (inputPath.startsWith("/")? inputPath.substring(1) : inputPath) : null;
		String[] parts = path != null? path.split("/") : null;
		if(parts != null) {
			Integer length = Integer.valueOf(parts.length);
			return routers.containsKey(length) ?
					routers.get(length).contains(criteria) : false;
		}
		return false;
	}

	@Override
	public void hierarchy(Map<String, Integer> map) {
		map.put("splitPathLength", routers.size());
		routers.entrySet().stream().forEach(entry -> entry.getValue().hierarchy(map));
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
		String inputPath = entity.path;
		String path = inputPath != null? (inputPath.startsWith("/")? inputPath.substring(1) : inputPath) : null;
		String[] parts = path != null? path.split("/") : null;
		if(parts != null) {
			Integer length = Integer.valueOf(parts.length);
			routers.get(length).remove(entity);
		}
	}
}
