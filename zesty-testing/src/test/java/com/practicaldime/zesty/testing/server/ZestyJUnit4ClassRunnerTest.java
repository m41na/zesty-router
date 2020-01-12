package com.practicaldime.zesty.testing.server;

import com.practicaldime.zesty.app.AppProvider;
import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.app.IServer;
import org.apache.commons.cli.Options;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import static com.practicaldime.zesty.app.AppOptions.applyDefaults;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

@RunWith(ZestyJUnit4ClassRunner.class)
public class ZestyJUnit4ClassRunnerTest {

    public static final Integer PORT = 9099;
    public static final String HOST = "127.0.0.1";
    public static final Map<String, String> props = new HashMap<>();

    private BiFunction<Integer, Integer, Integer> addition = (x, y) -> x + y;

    public String getUrl(String endpoint) {
        return String.format("http://%s:%d/%s", HOST, PORT, endpoint);
    }

    @ZestyProvider
    public IServer provider() {
        Map<String, String> config = applyDefaults(new Options(), new String[]{});
        config.put("appctx", "/");
        IServer server = ((AppProvider) props -> AppServer.instance(props)).provide(config);
        server.get("/hello", (req, res, done) -> done.resolve(CompletableFuture.runAsync(() -> {
            res.send("hello from server");
        })));
        server.listen(PORT, HOST);
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
