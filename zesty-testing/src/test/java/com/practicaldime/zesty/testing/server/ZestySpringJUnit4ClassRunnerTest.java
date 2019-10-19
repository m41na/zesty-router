package com.practicaldime.zesty.testing.server;

import com.practicaldime.zesty.app.AppServer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(ZestySpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ZestySpringJUnit4ClassRunnerTest.ZestyTestConfig.class})
@TestPropertySource("/test-file.properties")
public class ZestySpringJUnit4ClassRunnerTest {

    @Value("${x}")
    private int x;
    @Value("${y}")
    private int y;
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
        assertEquals("addition", 300, addition.apply(x, y).intValue());
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

    @Configuration
    public static class ZestyTestConfig {
    }
}