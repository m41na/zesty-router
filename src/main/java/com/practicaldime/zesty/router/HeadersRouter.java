package com.practicaldime.zesty.router;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HeadersRouter implements Router{

	private List<Route> routes = new ArrayList<>();
	
	@Override
	public void accept(RouteSearch input) {
		List<Route> pool = new ArrayList<>(routes);
		for(Iterator<Route> iter = pool.iterator(); iter.hasNext();) {
			Route mapping = iter.next();
			//is 'content-type' declared in mapping route?
			if(mapping.contentType != null && mapping.contentType.trim().length() > 0) {
				String reqHeader = input.requestAttrs.getHeader("Content-Type");
				if(reqHeader != null) {
					if(!reqHeader.contains(mapping.contentType)) {
						iter.remove();
						continue;
					}
				}
				else {
					iter.remove();
					continue;
				}
			}
			//is 'accept' declared in mapping route?
			if(mapping.accept != null && mapping.accept.trim().length() > 0) {
				String reqHeader = input.requestAttrs.getHeader("Accept");
				if(reqHeader != null) {
					if(!reqHeader.contains(mapping.accept)) {
						iter.remove();
						continue;
					}
				}
				else {
					iter.remove();
					continue;
				}
			}
			//are there headers in the mapping route that match the inputs?
			if(!mapping.headers.isEmpty()) {
				Map<String, String> headers = mapping.headers;
				for(String key : headers.keySet()) {
					if(input.requestAttrs.headers.containsKey(key)) {
						if(!input.requestAttrs.getHeader(key).contains(headers.get(key))) {
							iter.remove();
							break;
						}
					}
				}
			}
			//set first matched route if it exists in the input object
			input.result = pool.size() > 0? pool.get(0) : null;
		}
	}

	@Override
	public void addRoute(Route route) {
		this.routes.add(route);
	}
}
