package com.practicaldime.zesty.router;

import static org.junit.Assert.*;

import org.apache.commons.text.RandomStringGenerator;
import org.junit.Test;

public class RequestRouterTest {

	private RequestRouter router;
	private final RandomStringGenerator rand = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
	
	public RequestRouterTest() {
		router = new RequestRouter(new MethodRouter());		
		//add todo routes
		router.addRoute(RouteBuilder.create(rand.generate(10), "/todos", "get", null, "application/json"));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/todos", "post", "application/x-www-form-urlencoded", "application/json"));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/todos/{id}", "get", "application/json", null));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/todos/{id}/status", "get", "application/json", null));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/todos/{id}/name", "put", "application/x-www-form-urlencoded", "application/json"));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/todos/{id}/done", "put", "application/x-www-form-urlencoded", "application/json"));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/todos/{id}", "delete"));
		//add blog routes
		router.addRoute(RouteBuilder.create(rand.generate(10), "/blog", "post", "application/json", "application/json"));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/blog/{bid}/author", "post", "application/json", "application/json"));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/blog/{bid}", "get", "application/json", null));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/blog/{bid}/author/{uid}", "get", "application/json", null));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/blog/{bid}", "put", "application/json", "application/json"));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/blog/{bid}/author/{uid}", "put", "application/json", "application/json"));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/blog/{bid}", "delete", "application/json", null));
		router.addRoute(RouteBuilder.create(rand.generate(10), "/blog/{bid}/author/{uid}", "delete", "application/json", null));
	}
	
	@Test
	public void testSearchRoute() {
		Request getTodo = new Request();
		getTodo.method = "get";
		getTodo.url = "/todos/20";
		getTodo.headers.put("Accepts", new String[] {"application/json"});
		//search
		RouteSearch getTodoFound = router.searchRoute(getTodo);
		assertNotNull("Expecting result found", getTodoFound.result);
		assertEquals("Exxpecting 1 param", 1, getTodoFound.pathParams.size());
		
		Request getStatus = new Request();
		getStatus.method = "get";
		getStatus.url = "/todos/20/status";
		getStatus.headers.put("Accepts", new String[] {"application/json"});
		//search
		RouteSearch getStatusFound = router.searchRoute(getStatus);
		assertNotNull("Expecting result found", getStatusFound.result);
		assertEquals("Exxpecting 1 param", 1, getStatusFound.pathParams.size());
		
		Request getAuthor = new Request();
		getAuthor.method = "get";
		getAuthor.url = "/blog/20/author/4";
		getAuthor.headers.put("Accepts", new String[] {"application/json"});
		//search
		RouteSearch getAuthorFound = router.searchRoute(getAuthor);
		assertNotNull("Expecting result found", getAuthorFound.result);
		assertEquals("Exxpecting 2 param", 2, getAuthorFound.pathParams.size());
		
		Request delAuthor = new Request();
		delAuthor.method = "delete";
		delAuthor.url = "/blog/10/author/6";
		delAuthor.headers.put("Accepts", new String[] {"application/json"});
		//search
		RouteSearch delAuthorFound = router.searchRoute(delAuthor);
		assertNotNull("Expecting result found", delAuthorFound.result);
		assertEquals("Exxpecting 2 param", 2, delAuthorFound.pathParams.size());
	}
}
