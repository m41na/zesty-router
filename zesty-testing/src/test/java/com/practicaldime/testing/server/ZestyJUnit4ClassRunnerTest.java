package com.practicaldime.testing.server;

import com.practicaldime.zesty.app.AppServer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(ZestyJUnit4ClassRunner.class)
public class ZestyJUnit4ClassRunnerTest {

    private BiFunction<Integer, Integer, Integer> addition = (x, y) -> x + y;

    @ZestyProvider
    public AppServer provider() {
        AppServer server = new AppServer();
        server.router().get("/hello", (req, res, done) -> done.resolve(CompletableFuture.runAsync(() -> {
            res.send("hello from server");
        })));
        server.listen(9099, "localhost");
        return server;
    }

    @Test
    public void testAddition() {
        System.out.println("executing testAddition");
        assertEquals("addition", 8, addition.apply(5, 3).intValue());
    }

    @Test
    public void testHelloFromServer(HttpClient client) throws InterruptedException, ExecutionException, TimeoutException {
        ContentResponse response = client.newRequest("http://localhost:9099/hello")
                .timeout(3, SECONDS)
                .send();
        assertEquals("Contains 'hello from server'", true, response.getContentAsString().contains("hello from server"));
    }

    @Test
    public void testHelloFromServer2(HttpClient client) throws InterruptedException, ExecutionException, TimeoutException {
        ContentResponse response = client.newRequest("http://localhost:9099/hello")
                .timeout(3, SECONDS)
                .send();
        assertFalse("Contains 'hello from server++'", response.getContentAsString().contains("hello from server++"));
    }
}