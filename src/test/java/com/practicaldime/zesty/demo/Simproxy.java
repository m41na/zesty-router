package com.practicaldime.zesty.demo;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.proxy.BalancerServlet;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;

public class Simproxy {

    private static final String configHome = System.getProperty("user.dir");
    private static final int timeout = 30000;
    private static final int port = 8080;
    private static final int poolSize = 50;
    private static final String host = "localhost";
    private static final String resourceBase = "build";

    public static void main(String[] args) throws Exception {
        Server server = newServer();
        HttpConfiguration http = newHttpConfiguration();
        ServerConnector connector = newConnector(server, http);
        server.addConnector(connector);

        //add handler
        ServletContextHandler contextHandler = new ServletContextHandler(null, "/", false, false);

        //add servlet
        addBalancerServlet(contextHandler);

        //add handlers
        HandlerList handlers = new HandlerList();
        //handlers.setHandlers(new Handler[]{newResourceHandler(resourceBase), contextHandler, new DefaultHandler()});
        //OR
        addDefaultResourceHandler(contextHandler, resourceBase);
        handlers.setHandlers(new Handler[]{contextHandler, new DefaultHandler()});
        //set handlers
        server.setHandler(handlers);

        //configure extras
        addSecureSupport(http, server);
        addJmxSupport(server);
        try {
            server.start();
            // server.join() the will make the current thread join and wait until the server is done executing.
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            server.stop();
        }
    }

    private static void addSecureSupport(HttpConfiguration httpConfig, Server server) {
        Resource keystore = Resource.newClassPathResource("/keystore");
        if (keystore != null && keystore.exists()) {
            // if a keystore for a SSL certificate is available, start a SSL
            // connector on port 8443.
            SslContextFactory sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStorePath(configHome + "/src/test/config/etc/keystore");
            sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
            sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
            sslContextFactory.setTrustStorePath(configHome + "/src/test/config/etc/keystore");
            sslContextFactory.setTrustStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
            sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA",
                    "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                    "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                    "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                    "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                    "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

            // SSL HTTP Configuration
            HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
            httpsConfig.addCustomizer(new SecureRequestCustomizer());

            ServerConnector sslConnector = new ServerConnector(server,
                    new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                    new HttpConnectionFactory(httpsConfig));
            sslConnector.setPort(8443);
            server.addConnector(sslConnector);
        }
    }

    private static void addJmxSupport(Server server) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
        server.addBean(mBeanContainer);
    }

    private static HttpConfiguration newHttpConfiguration() {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(8443);
        httpConfig.setOutputBufferSize(32768);
        httpConfig.setRequestHeaderSize(8192);
        httpConfig.setResponseHeaderSize(8192);
        httpConfig.setSendServerVersion(true);
        httpConfig.setSendDateHeader(false);
        return httpConfig;
    }

    private static ServerConnector newConnector(Server server, HttpConfiguration http) {
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(http));
        connector.setIdleTimeout(timeout);
        connector.setHost(host);
        connector.setPort(port);
        return connector;
    }

    private static Server newServer() {
        Server server = new Server(configureThreadPool());
        server.addBean(new ScheduledExecutorScheduler(null, false));
        return server;
    }

    private static QueuedThreadPool configureThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(poolSize);
        return threadPool;
    }

    private static void addProxyServlet(ServletContextHandler context) {
        ServletHolder proxyServlet = new ServletHolder(ProxyServlet.Transparent.class);
        proxyServlet.setInitParameter("proxyTo", "http://localhost:8090");
        proxyServlet.setInitParameter("prefix", "/api");
        context.addServlet(proxyServlet, "/api/*");
    }

    private static void addBalancerServlet(ServletContextHandler context) {
        ServletHolder balancerServlet = new ServletHolder(BalancerServlet.class);
        balancerServlet.setInitParameter("balancerMember.jetty0.proxyTo", "http://localhost:8081");
        balancerServlet.setInitParameter("balancerMember.jetty1.proxyTo", "http://localhost:8082");
        balancerServlet.setInitParameter("prefix", "/api");
        context.addServlet(balancerServlet, "/api/*");
    }

    private static void addDefaultResourceHandler(ServletContextHandler context, String resourceBase) {
        DefaultServlet aServlet = new DefaultServlet();
        ServletHolder aHolder = new ServletHolder(aServlet);
        aHolder.setInitParameter("resourceBase", resourceBase);
        aHolder.setInitParameter("welcomeFile", "index.html");
        context.addServlet(aHolder, "/*");
    }

    private static void addDefaultResourceHandlers(ServletContextHandler servletHandler) {
        // Configuration for serving /A/* from X/V/A
        DefaultServlet aServlet = new DefaultServlet();
        ServletHolder aHolder = new ServletHolder(aServlet);
        aHolder.setInitParameter("resourceBase", resourceBase);
        aHolder.setInitParameter("pathInfoOnly", "true");
        servletHandler.addServlet(aHolder, "/*");

        // Configuration for serving /B/* from Q/Z/B
        DefaultServlet bServlet = new DefaultServlet();
        ServletHolder bHolder = new ServletHolder(bServlet);
        bHolder.setInitParameter("resourceBase", "build/");
        bHolder.setInitParameter("pathInfoOnly", "true");
        servletHandler.addServlet(bHolder, "/build/*");
    }

    private static ResourceHandler newResourceHandler(String resourceBase) {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true); //should be 'false' ideally
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase(resourceBase);
        return resourceHandler;
    }

    private static SessionHandler sqlSessionHandler(String driver, String url) {
        SessionHandler sessionHandler = new SessionHandler();
        SessionCache sessionCache = new DefaultSessionCache(sessionHandler);
        sessionCache.setSessionDataStore(jdbcDataStoreFactory(driver, url).getSessionDataStore(sessionHandler)
        );
        sessionHandler.setSessionCache(sessionCache);
        sessionHandler.setHttpOnly(true);
        // make additional changes to your SessionHandler here
        return sessionHandler;
    }

    private static JDBCSessionDataStoreFactory jdbcDataStoreFactory(String driver, String url) {
        DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
        databaseAdaptor.setDriverInfo(driver, url);
        JDBCSessionDataStoreFactory jdbcSessionDataStoreFactory = new JDBCSessionDataStoreFactory();
        jdbcSessionDataStoreFactory.setDatabaseAdaptor(databaseAdaptor);
        return jdbcSessionDataStoreFactory;
    }

    static int freePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }
}
