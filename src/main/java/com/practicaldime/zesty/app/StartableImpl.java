package com.practicaldime.zesty.app;

import com.practicaldime.zesty.servlet.RouteFilter;
import com.practicaldime.zesty.session.SessionUtil;
import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.ViewEngineFactory;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.fcgi.server.proxy.FastCGIProxyServlet;
import org.eclipse.jetty.fcgi.server.proxy.TryFilesFilter;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.eclipse.jetty.servlets.CrossOriginFilter.*;
import static org.eclipse.jetty.util.resource.Resource.newClassPathResource;

public abstract class StartableImpl implements Startable {

    private static final Logger LOG = LoggerFactory.getLogger(StartableImpl.class);

    private final ViewEngine engine;
    private final ServletContextHandler servlets;
    private final Function<String, String> properties;
    private final Collection<ContextHandler> contexts;

    private final Map<String, String> wpcontext = new HashMap<>();
    private final Map<String, String> corscontext = new HashMap<>();

    private String splash = "/splash/shadow.txt";
    private String status = "stopped";
    private Consumer<Boolean> shutdown;

    public StartableImpl(ServletContextHandler servlets, Function<String, String> properties, ViewEngineFactory engineFactory, Collection<ContextHandler> contexts) {
        this.servlets = servlets;
        this.properties = properties;
        this.contexts = contexts;
        this.engine = this.initEngine(engineFactory);
    }

    @Override
    public String status() {
        return "server status is " + status;
    }

    @Override
    public ViewEngine engine() {
        if (engine == null) {
            throw new RuntimeException("The engine is not yet initialized");
        }
        return engine;
    }

    @Override
    public final ViewEngine initEngine(ViewEngineFactory engineFactory) {
        String view = this.properties.apply("engine");
        switch (view) {
            case "jtwig":
                return engineFactory.engine(view, properties.apply("templates"), "html", "");
            case "freemarker":
                return engineFactory.engine(view, properties.apply("templates"), "ftl", "");
            case "handlebars":
            case "ejs":
            case "react":
                return engineFactory.engine(view, properties.apply("templates"), "js", properties.apply("lookup"));
            default:
                return engineFactory.engine(view, properties.apply("templates"), "", properties.apply("lookup"));
        }
    }

    @Override
    public Startable banner(String splash) {
        this.splash = splash;
        return this;
    }

    @Override
    public Startable assets(String mapping, String folder) {
        if (!AppOptions.UNASSIGNED.equals(this.properties.apply("assets"))) {
            LOG.warn("To use this option, remove the 'assets' property from the initial properties object");
        } else {
            String pathspec = mapping.endsWith("/*") ? mapping : (mapping.endsWith("/") ? mapping + "*" : mapping + "/*");
            if (Boolean.parseBoolean(this.properties.apply("assets.default.servlet"))) {
                ServletHolder defaultServlet = createResourceServlet(folder);
                servlets.addServlet(defaultServlet, pathspec);
            } else {
                ContextHandler context = new ContextHandler();
                context.setContextPath(pathspec);
                context.setHandler(createResourceHandler(folder));
                contexts.add(context);
            }
        }
        return this;
    }

    // ************* console splash banner ****************** //
    private void banner() {
        try (InputStream is = getClass().getResourceAsStream(this.splash)) {
            int maxSize = 1024;
            byte[] bytes = new byte[maxSize];
            int size = is.read(bytes);
            System.out.printf("splash file is %d bytes in size of a max acceptable %d bytes%n", size, maxSize);
            LOG.info(new String(bytes, 0, size));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    // ************* WORDPRESS *****************//
    @Override
    public Startable wordpress(String home, String proxyTo) {
        this.wpcontext.put("activate", "true");
        this.wpcontext.put("resourceBase", home);
        this.wpcontext.put("welcomeFile", "index.php");
        this.wpcontext.put("proxyTo", proxyTo);
        this.wpcontext.put("scriptRoot", home);
        return this;
    }

    // ************* CORS Headers *****************//
    @Override
    public Startable cors(Map<String, String> cors) {
        this.corscontext.putAll(cors);
        return this;
    }

    // ************* START *****************//
    @Override
    public void listen(int port, String host) {
        listen(port, host, null);
    }

    @Override
    public void listen(int port, String host, Consumer<String> result) {
        try {
            status = "starting";
            // splash banner
            banner();

            // create server with thread pool
            QueuedThreadPool threadPool = createThreadPool();
            Server server = new Server(threadPool);

            // Scheduler
            server.addBean(new ScheduledExecutorScheduler());

            // HTTP Configuration
            HttpConfiguration http_config = new HttpConfiguration();
            http_config.setSecureScheme("https");
            http_config.setSecurePort(Integer.parseInt(this.properties.apply("https.port")));
            http_config.setOutputBufferSize(Integer.parseInt(this.properties.apply("https.outputBufferSize")));

            //add http connector (http_1.1 connection factory)
            ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(http_config));
            httpConnector.setHost(host);
            httpConnector.setPort(port);
            httpConnector.setIdleTimeout(Long.parseLong(this.properties.apply("https.idleTimeout"))); //milliseconds
            server.addConnector(httpConnector);

            // HTTPS Configuration
            HttpConfiguration https_config = new HttpConfiguration(http_config);
            SecureRequestCustomizer customizer = new SecureRequestCustomizer();
            customizer.setStsMaxAge(Long.parseLong(this.properties.apply("https.ssl.stsMaxAge")));
            customizer.setStsIncludeSubDomains(Boolean.parseBoolean(this.properties.apply("https.ssl.includeSubDomains")));
            https_config.addCustomizer(customizer);

            // configure alpn connection factory for http2
            ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
            alpn.setDefaultProtocol("h2");

            // Configure ssl context factory
            SslContextFactory sslContextFactory = createSslContextFactory(
                    this.properties.apply("https.keystore.classpath"),
                    this.properties.apply("https.keystore.password")
            );

            // SSL Connection Factory
            SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

            // add https connector (both http_1.1 and http_2 connection factory)
            ServerConnector http2Connector = new ServerConnector(server, ssl, alpn,
                    new HTTP2ServerConnectionFactory(https_config),
                    new HttpConnectionFactory(https_config));
            http2Connector.setPort(Integer.parseInt(this.properties.apply("https.port")));
            http2Connector.setIdleTimeout(Long.parseLong(this.properties.apply("https.idleTimeout"))); //milliseconds
            server.addConnector(http2Connector);

            // enable CORS
            if (Boolean.parseBoolean(Optional.ofNullable(this.properties.apply("cors")).orElse("false"))) {
                FilterHolder corsFilter = new FilterHolder(CrossOriginFilter.class);
                //add default values
                corsFilter.setInitParameter(ALLOWED_ORIGINS_PARAM, Optional.ofNullable(corscontext.get(ALLOWED_ORIGINS_PARAM)).orElse("*"));
                corsFilter.setInitParameter(ALLOWED_METHODS_PARAM, Optional.ofNullable(corscontext.get(ALLOWED_METHODS_PARAM)).orElse("GET,POST,PUT,DELETE,OPTIONS,HEAD"));
                corsFilter.setInitParameter(ALLOWED_HEADERS_PARAM, Optional.ofNullable(corscontext.get(ALLOWED_HEADERS_PARAM)).orElse("Content-Type,Accept,Origin"));
                corsFilter.setInitParameter(ALLOW_CREDENTIALS_PARAM, Optional.ofNullable(corscontext.get(ALLOW_CREDENTIALS_PARAM)).orElse("true"));
                corsFilter.setInitParameter(PREFLIGHT_MAX_AGE_PARAM, Optional.ofNullable(corscontext.get(PREFLIGHT_MAX_AGE_PARAM)).orElse("728000"));
                //add other user defined values that are not in the list of default keys
                List<String> skipKeys = Arrays.asList(
                        ALLOWED_ORIGINS_PARAM,
                        ALLOWED_METHODS_PARAM,
                        ALLOWED_HEADERS_PARAM,
                        ALLOW_CREDENTIALS_PARAM,
                        PREFLIGHT_MAX_AGE_PARAM);
                corscontext.keySet().stream().filter(key -> !skipKeys.contains(key)).forEach(key -> corsFilter.setInitParameter(key, corscontext.get(key)));
                corsFilter.setName("zesty-cors-filter");

                FilterMapping corsMapping = new FilterMapping();
                corsMapping.setFilterName("cross-origin");
                corsMapping.setPathSpec("*");
                servlets.addFilter(corsFilter, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
            }
            // add routes filter
            servlets.addFilter(new FilterHolder(new RouteFilter(this.getRoutes(), this.getObjectMapper())), "/*", EnumSet.of(DispatcherType.REQUEST));

            // configure context for servlets
            String appctx = this.properties.apply("appctx");
            servlets.setContextPath(appctx.endsWith("/*") ? appctx.substring(0, appctx.length() - 2) : appctx.endsWith("/") ? appctx.substring(0, appctx.length() - 1) : appctx);

            // configure resource handlers if resourcesBase is NOT null
            String resourceBase = this.properties.apply("assets");

            // configure DefaultServlet to serve static content
            if (!AppOptions.UNASSIGNED.equals(resourceBase)) {
                if (Boolean.parseBoolean(this.properties.apply("assets.default.servlet"))) {
                    ServletHolder defaultServlet = createResourceServlet(resourceBase);
                    servlets.addServlet(defaultServlet, "/*");
                }
            }

            // configure ResourceHandler to serve static content
            if (!AppOptions.UNASSIGNED.equals(resourceBase)) {
                if (!Boolean.parseBoolean(this.properties.apply("assets.default.servlet"))) {
                    ContextHandler resourceHandler = new ContextHandler("/*");
                    resourceHandler.setHandler(createResourceHandler(resourceBase));
                    contexts.add(resourceHandler);
                }
            }

            // collect all context handlers
            ContextHandlerCollection contextHandlers = new ContextHandlerCollection();
            contextHandlers.addHandler(servlets);

            // add activated context handler (say, for php with fcgi)
            if (Boolean.parseBoolean(this.wpcontext.get("activate"))) {
                contextHandlers.addHandler(createFcgiHandler(this.wpcontext));
            }

            //configure session handler if necessary
            SessionHandler sessionHandler = null;
            if (Boolean.parseBoolean(this.properties.apply("session.jdbc.enable"))) {
                String url = this.properties.apply("session.jdbc.url");
                String driver = this.properties.apply("session.jdbc.driver");
                sessionHandler = SessionUtil.sqlSessionHandler(driver, url);
            }

            // add handlers to the server
            List<Handler> linkedHandlers = new LinkedList<>();
            if (contexts.size() > 0) {
                ContextHandlerCollection resourceHandlers = new ContextHandlerCollection();
                contexts.stream().forEach(context -> resourceHandlers.addHandler(context));
                linkedHandlers.add(resourceHandlers);
            }
            linkedHandlers.add(servlets);
            if (sessionHandler != null) {
                linkedHandlers.add(sessionHandler);
            }
            linkedHandlers.add(new DefaultHandler());
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(linkedHandlers.toArray(new Handler[0]));
            server.setHandler(handlers);

            // add shutdown hook
            addRuntimeShutdownHook(server);

            // start and access server using ${protocol}://${host}:${port}
            server.start();
            status = "running";

            // add shutdown handler
            shutdown = (flag) -> {
                try {
                    if (flag && server.isRunning()) server.stop();
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    System.exit(1);
                }
            };

            //add acknowledge handler
            if (result != null)
                result.accept(String.format("AppServer is now listening on http://%s:%d. (Press CTRL+C to quit)", host, port));

            //if reached, join and await interruption
            server.join();
            status = "stopped";
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            status = "stopped";
            if (result != null) result.accept("AppServer startup interrupted because of -> " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public final void shutdown() {
        if (this.shutdown != null) {
            this.shutdown.accept(true);
        }
    }

    private QueuedThreadPool createThreadPool() {
        int poolSize = Integer.parseInt(this.properties.apply("poolSize"));
        int maxPoolSize = Integer.parseInt(this.properties.apply("maxPoolSize"));
        int keepAliveTime = Integer.parseInt(this.properties.apply("keepAliveTime"));
        return new QueuedThreadPool(maxPoolSize, poolSize, keepAliveTime);
    }

    private SslContextFactory createSslContextFactory(String keyfile, String keypass) throws IOException {
        // SSL Context Factory for HTTPS and HTTP/2
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStoreResource(newClassPathResource(keyfile));
        sslContextFactory.setKeyStorePassword(keypass);
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        return sslContextFactory;
    }

    private ResourceHandler createResourceHandler(String resourceBase) {
        ResourceHandler appResources = new ResourceHandler();
        appResources.setResourceBase(resourceBase);
        appResources.setDirectoriesListed(Boolean.parseBoolean(this.properties.apply("assets.dirAllowed")));
        appResources.setPathInfoOnly(Boolean.parseBoolean(this.properties.apply("assets.pathInfoOnly")));
        appResources.setEtags(Boolean.parseBoolean(this.properties.apply("assets.etags")));
        appResources.setAcceptRanges(Boolean.parseBoolean(this.properties.apply("assets.acceptRanges")));
        appResources.setCacheControl(this.properties.apply("assets.cacheControl"));
        appResources.setWelcomeFiles(new String[]{this.properties.apply("assets.welcomeFile")});
        return appResources;
    }

    private ServletHolder createResourceServlet(String resourceBase) {
        // DefaultServlet should be named 'default-${resourceBase}'
        ServletHolder defaultServlet = new ServletHolder("default-" + resourceBase, DefaultServlet.class);
        defaultServlet.setInitParameter("resourceBase", resourceBase);
        defaultServlet.setInitParameter("dirAllowed", this.properties.apply("assets.dirAllowed"));
        defaultServlet.setInitParameter("pathInfoOnly", this.properties.apply("assets.pathInfoOnly"));
        defaultServlet.setInitParameter("etags", this.properties.apply("assets.etags"));
        defaultServlet.setInitParameter("acceptRanges", this.properties.apply("assets.acceptRanges"));
        defaultServlet.setInitParameter("cacheControl", this.properties.apply("assets.cacheControl"));
        defaultServlet.setInitParameter("welcomeFile", this.properties.apply("assets.welcomeFile"));
        return defaultServlet;
    }

    private ServletContextHandler createFcgiHandler(Map<String, String> phpctx) {
        ServletContextHandler php_ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        php_ctx.setContextPath("/");
        php_ctx.setResourceBase(phpctx.get("resourceBase"));
        php_ctx.setWelcomeFiles(new String[]{phpctx.get("welcomeFile")});

        // add try filter
        FilterHolder tryHolder = new FilterHolder(new TryFilesFilter());
        tryHolder.setInitParameter("files", "$path /index.php?p=$path");
        php_ctx.addFilter(tryHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

        // Add default servlet (dest serve the html/css/js)
        ServletHolder defHolder = new ServletHolder("default", new DefaultServlet());
        defHolder.setInitParameter("dirAllowed", "false");
        php_ctx.addServlet(defHolder, "/");

        // add fcgi servlet for php scripts
        ServletHolder fgciHolder = new ServletHolder("fcgi", new FastCGIProxyServlet());
        fgciHolder.setInitParameter("proxyTo", phpctx.get("proxyTo"));
        fgciHolder.setInitParameter("prefix", "/");
        fgciHolder.setInitParameter("scriptRoot", phpctx.get("scriptRoot"));
        fgciHolder.setInitParameter("scriptPattern", "(.+?\\\\.php)");
        php_ctx.addServlet(fgciHolder, "*.php");
        return php_ctx;
    }

    private void addRuntimeShutdownHook(final Server server) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server.isStarted()) {
                server.setStopAtShutdown(true);
                try {
                    server.stop();
                } catch (Exception e) {
                    LOG.error("Error while shutting down jetty server", e);
                    throw new RuntimeException(e);
                }
            }
        }));
    }
}
