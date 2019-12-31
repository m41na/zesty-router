package com.practicaldime.zesty.demo;

import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.servlet.HandlerConfig;
import com.practicaldime.zesty.servlet.HandlerPromise;
import com.practicaldime.zesty.servlet.HandlerRequest;
import com.practicaldime.zesty.servlet.HandlerResponse;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.PushBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class H2PushApp {

    private static final Logger LOG = LoggerFactory.getLogger(H2PushApp.class);

    public static void main(String... args) {
        int port = 8088;
        String host = "localhost";

        start(port, host);
    }

    public static void start(int port, String host) {
        //NOTE: no leading '/' before assets path, but the leading '/' is required for templates path
        HandlerConfig config = cfg -> cfg.setAsyncSupported(true);
        AppServer app = AppServer.instance();
        app.assets("/assets", "src/test/resources/webapp/page/assets/").templates("/src/test/resources/webapp/page/")
                .get("/", config, (HandlerRequest request, HandlerResponse response, HandlerPromise promise) -> {
                    Request jettyRequest = Request.getBaseRequest(request);
                    PushBuilder pushBuilder = jettyRequest.newPushBuilder();
                    if (pushBuilder != null) {
                        pushBuilder
                                .path("index.css")
                                .addHeader("content-type", "text/css")
                                .path("index.js")
                                .addHeader("content-type", "text/javascript")
                                .push();
                    }
                    response.render("index.html", null);
                    promise.complete();
                }).servlet("/next", config, new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                AsyncContext ctx = req.startAsync();
                String assets = "/webapp/page/index.html";
                Request jettyRequest = Request.getBaseRequest(req);
                PushBuilder pushBuilder = jettyRequest.newPushBuilder();
                if (pushBuilder != null) {
                    pushBuilder
                            .path("index.css")
                            .addHeader("content-type", "text/css")
                            .path("index.js")
                            .addHeader("content-type", "text/javascript")
                            .push();
                }
                try (InputStream source = this.getClass().getResourceAsStream(assets)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    while (source.read(bytes) != -1) {
                        baos.write(bytes);
                    }
                    ByteBuffer buffer = ByteBuffer.allocate(baos.size());
                    buffer.put(baos.toByteArray());
                    buffer.rewind(); //resets buffer's position pointer to '0' before reading starts

                    ServletOutputStream out = resp.getOutputStream();
                    WritableByteChannel channel = Channels.newChannel(out);
                    out.setWriteListener(new WriteListener() {
                        @Override
                        public void onWritePossible() throws IOException {
                            while (out.isReady()) {
                                channel.write(buffer);
                                if (buffer.position() == buffer.limit()) {
                                    ctx.complete();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            ctx.complete();
                            t.printStackTrace(System.err);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    ctx.complete();
                }
            }

        }).servlet("/echo", config, new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                AsyncContext ctx = req.startAsync();

                ServletInputStream input = req.getInputStream();
                input.setReadListener(new ReadListener() {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    @Override
                    public void onDataAvailable() throws IOException {
                        byte[] bytes = new byte[1024];
                        while (input.isReady() && input.read(bytes) != -1) {
                            baos.write(bytes);
                        }
                    }

                    @Override
                    public void onAllDataRead() throws IOException {
                        //simply wrap input data into a byte buffer
                        ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
                        buffer.rewind();

                        //echo back data in byte buffer
                        resp.setHeader("content-type", "application/json");
                        ServletOutputStream out = resp.getOutputStream();
                        WritableByteChannel channel = Channels.newChannel(out);
                        out.setWriteListener(new WriteListener() {
                            @Override
                            public void onWritePossible() throws IOException {
                                while (out.isReady()) {
                                    channel.write(buffer);
                                    if (buffer.position() == buffer.limit()) {
                                        ctx.complete();
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                ctx.complete();
                                t.printStackTrace(System.err);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable t) {
                        ctx.complete();
                        t.printStackTrace(System.err);
                    }
                });
            }
        }).listen(port, host, (msg) -> LOG.info(msg));
    }
}
