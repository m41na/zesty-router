package com.practicaldime.router.http.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.proxy.BalancerServlet;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.session.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;

public class ProxyApp {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyApp.class);
    private static final String rootDir = System.getProperty("user.dir");
    private static final String configLocation = "config/application.default.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void start(ProxyConfig overrides) throws Exception {
        LOG.info("loading configuration");
        ProxyConfig config = ProxyConfig.load(rootDir, configLocation, mapper);
        if (overrides != null) overrides.thisOnNotNullToThat(config);
        Server server = newServer(config.poolSize);
        HttpConfiguration http = newHttpConfiguration();
        ServerConnector connector = newConnector(server, config.host, config.port, config.timeout, http);
        server.addConnector(connector);

        LOG.info("creating proxy context handler");
        ServletContextHandler proxyContext = new ServletContextHandler(null, config.appctx, false, false);
        proxyContext.setSessionHandler(sqlSessionHandler(config.session.driver, config.session.url));

        LOG.info("adding proxy members");
        if (config.balancer.members.size() > 0) {
            addBalancerServlet(proxyContext, config);
        } else {
            addProxyServlet(proxyContext, config);
        }

        LOG.info("adding resource handlers");
        addDefaultResourceHandlers(proxyContext, config);

        //bundle up handlers
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{proxyContext, new DefaultHandler()});
        //set handlers
        server.setHandler(handlers);

        LOG.info("configuring ssl and jmx support");
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
            LOG.info("ssl not configured yet");
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

    private static ServerConnector newConnector(Server server, String host, int port, long timeout, HttpConfiguration http) {
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(http));
        connector.setIdleTimeout(timeout);
        connector.setHost(host);
        connector.setPort(port);
        return connector;
    }

    private static Server newServer(int poolSize) {
        Server server = new Server(configureThreadPool(poolSize));
        server.addBean(new ScheduledExecutorScheduler(null, false));
        return server;
    }

    private static QueuedThreadPool configureThreadPool(int size) {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(size);
        return threadPool;
    }

    private static void addProxyServlet(ServletContextHandler context, ProxyConfig config) {
        ServletHolder proxyServlet = new ServletHolder(ProxyServlet.Transparent.class);
        proxyServlet.setInitParameter("proxyTo", config.proxy.url);
        proxyServlet.setInitParameter("prefix", config.proxy.prefix);
        context.addServlet(proxyServlet, config.proxy.pathspec);
    }

    private static void addBalancerServlet(ServletContextHandler context, ProxyConfig config) {
        ServletHolder balancerServlet = new ServletHolder(BalancerServlet.class);
        for (ProxyConfig.Balancer.Member member : config.balancer.members) {
            balancerServlet.setInitParameter(String.format("balancerMember.%s.proxyTo", member.name), member.url);
        }
        balancerServlet.setInitParameter("prefix", config.balancer.prefix);
        context.addServlet(balancerServlet, config.balancer.pathspec);
    }

    private static void addDefaultResourceHandlers(ServletContextHandler context, ProxyConfig config) {
        for (ProxyConfig.Static resource : config.resources) {
            DefaultServlet aServlet = new DefaultServlet();
            ServletHolder aHolder = new ServletHolder(aServlet);
            aHolder.setInitParameter("resourceBase", resource.assets);
            aHolder.setInitParameter("dirAllowed", "false");
            aHolder.setInitParameter("pathInfoOnly", "true");
            aHolder.setInitParameter("welcomeFile", "index.html");
            context.addServlet(aHolder, resource.pathspec);
        }
    }

    private static SessionHandler sqlSessionHandler(String driver, String url) {
        SessionHandler sessionHandler = new SessionHandler();
        SessionCache sessionCache = new DefaultSessionCache(sessionHandler);
        sessionCache.setSessionDataStore(jdbcDataStoreFactory(driver, url).getSessionDataStore(sessionHandler));
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

    public static int freePort() {
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
