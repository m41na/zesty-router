package com.practicaldime.zesty.route;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jetty.server.Request;

public class AppRouter implements RouteMatcher{

    private final Map<String, RouteMatcher> routeMatchers = new HashMap<>();
    
    public AppRouter() {
    	routeMatchers.put("GET", new MethodMatcher());
    	routeMatchers.put("POST", new MethodMatcher());
    	routeMatchers.put("PUT", new MethodMatcher());
    	routeMatchers.put("DELETE", new MethodMatcher());
    	routeMatchers.put("HEAD", new MethodMatcher());
    	routeMatchers.put("OPTIONS", new MethodMatcher());
    	routeMatchers.put("TRACE", new MethodMatcher());
    }
    
    public void addRoute(AppRoute route) {
    	switch(route.method.toUpperCase()){
    		case "GET":{
    			routeMatchers.get("get").addRoute(route);
    			break;
    		}
    		case "POST":{
    			routeMatchers.get("post").addRoute(route);
    			break;
    		}
    		case "PUT":{
    			routeMatchers.get("put").addRoute(route);
    			break;
    		}
    		case "DELETE":{
    			routeMatchers.get("delete").addRoute(route);
    			break;
    		}
    		case "HEAD":{
    			routeMatchers.get("head").addRoute(route);
    			break;
    		}
    		case "OPTIONS":{
    			routeMatchers.get("options").addRoute(route);
    			break;
    		}
    		case "TRACE":{
    			routeMatchers.get("trace").addRoute(route);
    			break;
    		}
    		default: {
    			break;
    		}
    	}
    }
    
    public AppRoute match(Request search) {
    	String key = search.getMethod().toUpperCase();
    	return routeMatchers.containsKey(key)? routeMatchers.get(key).match(search) : null;
    }
    
    public static class MethodMatcher implements RouteMatcher{
    	
    	private final Map<Integer, RouteMatcher> routes = new HashMap<>();

		@Override
		public void addRoute(AppRoute route) {
			String path = route.path;
			if(path.startsWith("/")) path = path.substring(1);
			String parts[] = path.split("/");
			Integer key = Integer.valueOf(parts.length);
			if(!routes.containsKey(key)){
				routes.put(key, new SplitPathMatcher());
			}
			routes.get(key).addRoute(route);
		}

		@Override
		public AppRoute match(Request search) {
			String path = search.getRequestURI();
			if(path.startsWith("/")) path = path.substring(1);
			String parts[] = path.split("/");
			Integer key = Integer.valueOf(parts.length);
			return routes.containsKey(key)? routes.get(key).match(search) : null;
		}
    }
    
    public static class SplitPathMatcher implements RouteMatcher{

    	private final Map<Pattern, RouteMatcher> routes = new HashMap<>();
    	
		@Override
		public void addRoute(AppRoute route) {
			//TODO: implement this method
		}

		@Override
		public AppRoute match(Request search) {
			// TODO implement this method
			return null;
		}    	
    }
    
    public static class PathRegexMatcher implements RouteMatcher{

		@Override
		public void addRoute(AppRoute route) {
			
		}

		@Override
		public AppRoute match(Request search) {
			// TODO Auto-generated method stub
			return null;
		}    	
    }
}
