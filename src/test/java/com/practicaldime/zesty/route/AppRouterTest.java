package com.practicaldime.zesty.route;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jetty.server.Request;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class AppRouterTest {

	private final AppRouter router = new AppRouter();
	
	public AppRouterTest() {
		router.addRoute(new AppRoute("/book", "post", "application/json", "application/json"));
        router.addRoute(new AppRoute("/book/:id", "get", "application/json", ""));
        router.addRoute(new AppRoute("/book", "get", "application/json", ""));
        router.addRoute(new AppRoute("/book/:id", "put", "application/json", "application/json"));
        router.addRoute(new AppRoute("/book/:id", "delete", "application/json", ""));
        
        router.addRoute(new AppRoute("/book", "get", "application/json", "application/json"));
        router.addRoute(new AppRoute("/book/:id", "get", "application/json", "application/json"));
        router.addRoute(new AppRoute("/book/:id/author", "get", "application/json", ""));
        router.addRoute(new AppRoute("/book/:id/author/:uid", "get", "application/json", ""));
        router.addRoute(new AppRoute("/book/:id/author/:uid/name", "get", "application/json", ""));
        router.addRoute(new AppRoute("/book/:id/author/:uid/address/:city", "get", "application/json", ""));
        
        router.addRoute(new AppRoute("/book", "post", "application/json", "application/json"));
        router.addRoute(new AppRoute("/book/:id", "post", "application/json", "application/json"));
        router.addRoute(new AppRoute("/book/:id/author", "post", "application/json", ""));
        router.addRoute(new AppRoute("/book/:id/author/:uid", "post", "application/json", ""));
        router.addRoute(new AppRoute("/book/:id/author/:uid/name", "post", "application/json", ""));
        router.addRoute(new AppRoute("/book/:id/author/:uid/address/:city", "post", "application/json", ""));
        
        router.addRoute(new AppRoute("/book", "put", "application/json", "application/json"));
        router.addRoute(new AppRoute("/book/:id", "put", "application/json", "application/json"));
        router.addRoute(new AppRoute("/book/:id/author", "put", "application/json", ""));
        router.addRoute(new AppRoute("/book/:id/author/:uid", "put", "application/json", ""));
        router.addRoute(new AppRoute("/book/:id/author/:uid/name", "put", "application/json", ""));
        router.addRoute(new AppRoute("/book/:id/author/:uid/address/:city", "put", "application/json", ""));
	}
	
	@Test
    public void testMatchBookAuthorNameRoute() {
        AppRoute incoming = new AppRoute("/book/1234/author/6789/name", "get", "application/json", "");
        Request request = new MockRoute(null, null, incoming);
        AppRoute match = router.match(request);
        assertTrue(match != null);
        assertEquals("Expecting '1234'", "1234", match.pathParams.get(":id"));
        assertEquals("Expecting '6789'", "6789", match.pathParams.get(":uid"));
    }
}
