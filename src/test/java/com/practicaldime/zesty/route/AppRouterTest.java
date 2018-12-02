package com.practicaldime.zesty.route;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.server.Request;
import org.junit.Test;

import com.practicaldime.zesty.route.AppRoute;
import com.practicaldime.zesty.route.AppRoutes;

import static org.junit.Assert.*;

public class AppRouterTest {

    private final AppRoutes router = AppRoutes.instance();

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
        AppRoute match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting '1234'", "1234", match.pathParams.get(":id"));
        assertEquals("Expecting '6789'", "6789", match.pathParams.get(":uid"));
    }

    @Test
    public void testMatchBookAuthorCityRoute() {
        AppRoute incoming = new AppRoute("/book/9876/author/5432/address/chicago", "get", "application/json", "");
        Request request = new MockRoute(null, null, incoming);
        AppRoute match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting '9876'", "9876", match.pathParams.get(":id"));
        assertEquals("Expecting '5432'", "5432", match.pathParams.get(":uid"));
        assertEquals("Expecting 'chicago'", "chicago", match.pathParams.get(":city"));
    }
    
    

    @Test
    public void testMatchBookPathWithDifferentMathods() {
        AppRoute incoming = new AppRoute("/book", "get", "application/json", "application/json");
        Request request = new MockRoute(null, null, incoming);
        AppRoute match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting 'get'", "get", match.method);
        
        incoming = new AppRoute("/book", "post", "application/json", "application/json");
        request = new MockRoute(null, null, incoming);
        match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting 'post'", "post", match.method);
        
        incoming = new AppRoute("/book", "put", "application/json", "application/json");
        request = new MockRoute(null, null, incoming);
        match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting 'put'", "put", match.method);
    }
    
    @Test
    public void testRegexPathMatching() {
        String input = "/book/9876/author/5432/address/chicago";
        String route = "/book/:id/author/:uid/address/:city";
        Map<String, String> params = new HashMap<>();
        
        //step 1. identify param patterns in the configured route (:.*?)[\b\W]
        //step 2. replace the patterns identified with literals (:.*?)
        //step 3. resulting string from step 3 becomes regex to match agains input uri
        String paramRegex = "((:\\w+)(\\W|\\b)??)";
        String inputRegex = route.replaceAll(paramRegex, "(\\\\w+?)");
        Pattern inputPattern = Pattern.compile("^" + inputRegex + "$");
        
        //step 4. match identified params from route
        Pattern paramPattern = Pattern.compile(paramRegex);
        Matcher paramMatcher = paramPattern.matcher(route);

        System.out.printf("derived route pattern [%s] from route [%s] to match against input [%s]%n", inputPattern.pattern(), route, input);
        
        Matcher inputMatcher = inputPattern.matcher(input);
        if (inputMatcher.matches()) {
            int index = 1;
            while (paramMatcher.find()) {
                String param = paramMatcher.group(1);
                String value = inputMatcher.group(index++);
                params.put(param, value);
            }
            System.out.printf("the input [%s] matched the pattern [%s]", input, inputPattern.pattern());
        }
        System.out.println(params);
    }
}
