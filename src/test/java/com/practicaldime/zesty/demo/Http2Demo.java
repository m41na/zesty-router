package com.practicaldime.zesty.demo;

import org.eclipse.jetty.alpn.ALPN;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.PushBuilder;
import java.io.File;
import java.io.IOException;

public class Http2Demo {

    public static void main(String... args) throws Exception {
        String keystorePath = System.getProperty("keystore.path", "/keystore.jks");
        File keystoreFile = new File(Http2Demo.class.getResource(keystorePath).toURI().getPath());

        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8088);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new Servlet()), "/");
        server.setHandler(context);

        // HTTP Configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);

        // SSL Context Factory for HTTPS and HTTP/2
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
        sslContextFactory.setKeyStorePassword("OBF:1x901wu01v1x20041ym71zzu1v2h1wue1x7u");
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);

        // HTTPS Configuration
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        // configure alpn connection factory for http2
        ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol("h2");

        // SSL Connection Factory
        SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

        // HTTP/2 Connector
        ServerConnector http2Connector = new ServerConnector(server, ssl, alpn,
                new HTTP2ServerConnectionFactory(https_config),
                new HttpConnectionFactory(https_config));
        http2Connector.setPort(8443);
        server.addConnector(http2Connector);

        ALPN.debug = false;

        server.start();
        server.join();
    }

    public static class Servlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            PushBuilder pushBuilder = req.newPushBuilder();
            resp.setContentType("text/plain");
            resp.getWriter().write("Is request async? " + req.isAsyncSupported() + "\nCan server push resources using h2 push feature? " + Boolean.valueOf(pushBuilder != null));
        }
    }
}
