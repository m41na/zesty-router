package com.practicaldime.zesty.testing.server;

import com.practicaldime.zesty.app.AppServer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

@RunWith(ZestyJUnit4ClassRunner.class)
public class ZestyJUnit4ClassRunnerTest {

    public static final Integer PORT = 9099;
    public static final String HOST = "127.0.0.1";
    public static final Properties props = new Properties();

    private BiFunction<Integer, Integer, Integer> addition = (x, y) -> x + y;

    public String getUrl(String endpoint){
        return String.format("http://%s:%d/%s", HOST, PORT, endpoint);
    }

    @ZestyProvider
    public AppServer provider() {
        props.put("host", HOST);
        props.put("port", PORT);
        AppServer server = new AppServer();
        server.router().get("/hello", (req, res, done) -> done.resolve(CompletableFuture.runAsync(() -> {
            res.send("hello from server");
        })));
        server.listen(Optional.ofNullable(Integer.parseInt(props.getProperty("port"))).orElse(PORT), props.getProperty("host", HOST));
        return server;
    }

    @Test
    public void testAddition() {
        System.out.println("executing testAddition");
        assertEquals("addition", 8, addition.apply(5, 3).intValue());
    }

    @Test
    public void testHelloFromServer(HttpClient client) throws InterruptedException, ExecutionException, TimeoutException {
        ContentResponse response = client.newRequest(getUrl("hello"))
                .timeout(3, SECONDS)
                .send();
        assertEquals("Contains 'hello from server'", true, response.getContentAsString().contains("hello from server"));
    }
}