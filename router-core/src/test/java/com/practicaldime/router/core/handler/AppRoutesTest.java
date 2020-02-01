package com.practicaldime.router.core.handler;

import com.practicaldime.router.core.routing.MethodRouter;
import com.practicaldime.router.core.routing.Routing;
import org.apache.commons.text.RandomStringGenerator;
import org.eclipse.jetty.server.Request;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class AppRoutesTest {

    private AppRouter router;

    public AppRoutesTest() {
        router = new AppRouter(new MethodRouter());
        //add todo routes
        RandomStringGenerator rand = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/todos", "get", null, "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/todos", "post", "application/x-www-form-urlencoded", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/todos/{id}", "get", "application/json", null));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/todos/{id}/status", "get", "application/json", null));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/todos/{id}/name", "put", "application/x-www-form-urlencoded", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/todos/{id}/done", "put", "application/x-www-form-urlencoded", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/todos/{id}", "delete"));
        //add blog routes
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/blog", "post", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/blog/{bid}/author", "post", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/blog/{bid}", "get", "application/json", null));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/blog/{bid}/author/{uid}", "get", "application/json", null));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/blog/{bid}", "put", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/blog/{bid}/author/{uid}", "put", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/blog/{bid}", "delete", "application/json", null));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/blog/{bid}/author/{uid}", "delete", "application/json", null));
        //add book routes
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book", "post", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}", "get", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book", "get", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}", "put", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}", "delete", "application/json", ""));

        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book", "get", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}", "get", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author", "get", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author/{uid}", "get", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author/{uid}/name", "get", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author/{uid}/address/{city}", "get", "application/json", ""));

        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/new", "post", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}", "post", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author", "post", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author/{uid}", "post", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author/{uid}/name", "post", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author/{uid}/address/{city}", "post", "application/json", ""));

        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book", "put", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}", "put", "application/json", "application/json"));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author", "put", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author/{uid}", "put", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author/{uid}/name", "put", "application/json", ""));
        router.add(Routing.RouteBuilder.create(rand.generate(10), "/book/{id}/author/{uid}/address/{city}", "put", "application/json", ""));
    }

    @Test
    public void testSearchRoute() {
        Routing.Attributes getTodo = new Routing.Attributes();
        getTodo.method = "get";
        getTodo.url = "/todos/20";
        getTodo.headers.put("Accept", "application/json");
        //search
        Routing.Search getTodoFound = router.searchRoute(getTodo);
        assertNotNull("Expecting result found", getTodoFound.result);
        assertEquals("Exxpecting 1 param", 1, getTodoFound.pathParams.size());

        Routing.Attributes getStatus = new Routing.Attributes();
        getStatus.method = "get";
        getStatus.url = "/todos/20/status";
        getStatus.headers.put("Accept", "application/json");
        //search
        Routing.Search getStatusFound = router.searchRoute(getStatus);
        assertNotNull("Expecting result found", getStatusFound.result);
        assertEquals("Exxpecting 1 param", 1, getStatusFound.pathParams.size());

        Routing.Attributes getAuthor = new Routing.Attributes();
        getAuthor.method = "get";
        getAuthor.url = "/blog/20/author/4";
        getAuthor.headers.put("Accept", "application/json");
        //search
        Routing.Search getAuthorFound = router.searchRoute(getAuthor);
        assertNotNull("Expecting result found", getAuthorFound.result);
        assertEquals("Exxpecting 2 param", 2, getAuthorFound.pathParams.size());

        Routing.Attributes delAuthor = new Routing.Attributes();
        delAuthor.method = "delete";
        delAuthor.url = "/blog/10/author/6";
        delAuthor.headers.put("Accept", "application/json");
        //search
        Routing.Search delAuthorFound = router.searchRoute(delAuthor);
        assertNotNull("Expecting result found", delAuthorFound.result);
        assertEquals("Exxpecting 2 param", 2, delAuthorFound.pathParams.size());
    }

    @Test
    public void testMatchBookAuthorNameRoute() {
        Routing.Route incoming = new Routing.Route("/book/1234/author/6789/name", "get", "application/json", null);
        Request request = new MockRoute(null, null, incoming);
        Routing.Search match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting '1234'", "1234", match.pathParams.get("id"));
        assertEquals("Expecting '6789'", "6789", match.pathParams.get("uid"));
    }

    @Test
    public void testMatchBookAuthorCityRoute() {
        Routing.Route incoming = new Routing.Route("/book/9876/author/5432/address/chicago", "get", "application/json", "");
        Request request = new MockRoute(null, null, incoming);
        Routing.Search match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting '9876'", "9876", match.pathParams.get("id"));
        assertEquals("Expecting '5432'", "5432", match.pathParams.get("uid"));
        assertEquals("Expecting 'chicago'", "chicago", match.pathParams.get("city"));
    }

    @Test
    public void testMatchBookPathWithDifferentMethods() {
        Routing.Route incoming = new Routing.Route("/book", "get", "application/json", "application/json");
        Request request = new MockRoute(null, null, incoming);
        Routing.Search match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting 'get'", "get", match.result.method.toLowerCase());

        incoming = new Routing.Route("/book", "post", "application/json", "application/json");
        request = new MockRoute(null, null, incoming);
        match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting 'post'", "post", match.result.method.toLowerCase());

        incoming = new Routing.Route("/book", "put", "application/json", "application/json");
        request = new MockRoute(null, null, incoming);
        match = router.search(request);
        assertTrue(match != null);
        assertEquals("Expecting 'put'", "put", match.result.method.toLowerCase());
    }

    @Test
    public void testRegexPathMatching() {
        String input = "/book/9876/author/5432/address/chicago";
        String route = "/book/{id}/author/{uid}/address/{city}";
        Map<String, String> params = new HashMap<>();

        //step 1. identify param patterns in the configured route (\\{.+?\\})
        //step 2. replace the patterns identified with literals (:.*?)
        //step 3. resulting string from step 3 becomes regex dest match agains input uri
        String paramRegex = "(\\{.+?\\})";
        String inputRegex = route.replaceAll(paramRegex, "\\\\{(.+?)\\\\}").replaceAll("\\/", "\\\\/");
        Pattern inputPattern = Pattern.compile("^" + inputRegex + "$");

        //step 4. match identified params from route
        Pattern paramPattern = Pattern.compile(paramRegex);
        Matcher paramMatcher = paramPattern.matcher(route);

        System.out.printf("derived route pattern [%s] from route [%s] dest match against input [%s]%n", inputPattern.pattern(), route, input);

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

    @Test
    public void testDisplayRoutesHierarchy() {
        List<String> info = new LinkedList<>();
        router.info(info, "");
        System.out.println(info);
    }
}
