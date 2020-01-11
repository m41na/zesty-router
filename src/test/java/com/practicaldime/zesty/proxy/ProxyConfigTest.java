package com.practicaldime.zesty.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.app.IServer;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProxyConfigTest {

    String rootDir = ".";
    String configLocation = "config/application.default.json";
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testLoadDefaultConfig() throws IOException {
        System.setProperty("config.file", "");
        ProxyConfig config = ProxyConfig.load(rootDir, configLocation, mapper);
        assertEquals("Expecting \"http://localhost:8080\"", "http://localhost:8080", config.proxy.url);
        assertEquals("Expecting \"/api\"", "/api", config.appctx);
        assertEquals("Expecting \"2\"", 2, config.resources.size());
        assertEquals("Expecting \"www\"", "docs", config.resources.get(0).assets);
        assertEquals("Expecting \"www\"", "/docs", config.resources.get(0).context);
        assertEquals("Expecting \"www\"", "/docs/*", config.resources.get(0).pathspec);
    }

    @Test
    public void testLoadCustomConfigFromRelativeClasspath() throws IOException {
        System.setProperty("config.file", "config/application.json");
        ProxyConfig config = ProxyConfig.load(rootDir, configLocation, mapper);
        assertEquals("Expecting \"http://proxyhost:8081\"", "http://proxyhost:8081", config.proxy.url);
    }

    @Test
    public void testLoadCustomConfigFromAbsoluteClasspath() throws IOException {
        System.setProperty("config.file", "/config/application.json");
        ProxyConfig config = ProxyConfig.load(rootDir, configLocation, mapper);
        assertEquals("Expecting \"http://proxyhost:8081\"", "http://proxyhost:8081", config.proxy.url);
    }

    @Test
    public void testLoadCustomConfigFromRelativeFilePath() throws IOException {
        System.setProperty("config.file", "/src/test/resources/config/application.json");
        ProxyConfig config = ProxyConfig.load(rootDir, configLocation, mapper);
        assertEquals("Expecting \"http://proxyhost:8081\"", "http://proxyhost:8081", config.proxy.url);
    }

    @Test
    public void testLoadCustomConfigFromAbsoluteFilePath() throws IOException {
        System.setProperty("config.file", System.getProperty("user.dir") + "/src/test/resources/config/application.json");
        ProxyConfig config = ProxyConfig.load(rootDir, configLocation, mapper);
        assertEquals("Expecting \"http://proxyhost:8081\"", "http://proxyhost:8081", config.proxy.url);
    }

    @Test
    public void testLoadCustomConfigFromURLPath() throws IOException {
        int port = ProxyApp.freePort();
        runTestWithServer(port, (server, latch) -> {
            System.setProperty("config.file", "http://localhost:" + port + "/config");
            ProxyConfig config = null;
            try {
                config = ProxyConfig.load(rootDir, configLocation, mapper);
            } catch (IOException e) {
                e.printStackTrace(System.err);
                fail(e.getMessage());
            }
            assertEquals("Expecting \"http://proxyhost:8088\"", "http://proxyhost:8088", config.proxy.url);
            server.shutdown();
            latch.countDown();
        });

    }

    public void runTestWithServer(int port, BiConsumer<IServer, CountDownLatch> test) {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            IServer app = AppServer.instance();
            app.get("/config", (req, res, done) -> {
                res.send("{\n" +
                        "  \"proxy\": {\n" +
                        "    \"pathspec\": \"/*\",\n" +
                        "    \"prefix\": \"/\",\n" +
                        "    \"url\": \"http://proxyhost:8088\"\n" +
                        "  }\n" +
                        "}");
                done.complete();
            }).listen(port, "localhost", (message) -> {
                test.accept(app, latch);
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            });
        }).start();
    }
}
