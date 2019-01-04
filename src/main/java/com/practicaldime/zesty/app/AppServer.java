package com.practicaldime.zesty.app;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.management.remote.JMXServiceURL;
import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.fcgi.server.proxy.FastCGIProxyServlet;
import org.eclipse.jetty.fcgi.server.proxy.TryFilesFilter;
import org.eclipse.jetty.jmx.ConnectorServer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.practicaldime.zesty.basics.AppRoutes;
import com.practicaldime.zesty.extras.AppWsProvider;
import com.practicaldime.zesty.extras.AppWsServlet;
import com.practicaldime.zesty.router.MethodRouter;
import com.practicaldime.zesty.router.Route;
import com.practicaldime.zesty.router.Router;
import com.practicaldime.zesty.servlet.HandlerConfig;
import com.practicaldime.zesty.servlet.HandlerFilter;
import com.practicaldime.zesty.servlet.HandlerRequest;
import com.practicaldime.zesty.servlet.HandlerResponse;
import com.practicaldime.zesty.servlet.HandlerServlet;
import com.practicaldime.zesty.servlet.ReRouteFilter;
import com.practicaldime.zesty.view.ViewEngine;
import com.practicaldime.zesty.view.ftl.FtlViewEngine;
import com.practicaldime.zesty.view.string.StringViewEngine;
import com.practicaldime.zesty.view.twig.TwigViewEngine;

public class AppServer {

	private static final Logger LOG = LoggerFactory.getLogger(AppServer.class);

	private Server server;
	private AppRoutes routes;
	private String status = "stopped";
	private final Properties locals = new Properties();
	private final ThreadPoolExecutor threadPoolExecutor;
	private final Map<String, String> wpcontext = new HashMap<>();
	private final LifecycleSubscriber lifecycle = new LifecycleSubscriber();
	private final ServletContextHandler servlets = new ServletContextHandler(ServletContextHandler.SESSIONS);
	private static ViewEngine engine;

	public AppServer() {
		this(new HashMap<>());
	}

	public AppServer(Map<String, String> props) {
		this.assets(Optional.ofNullable(props.get("assets")).orElse("www"));
		this.appctx(Optional.ofNullable(props.get("appctx")).orElse("/"));
		this.engine(Optional.ofNullable(props.get("engine")).orElse("jtwig"));
		this.threadPoolExecutor = createThreadPoolExecutor();
	}

	public static ViewEngine engine() {
		if (engine == null) {
			throw new RuntimeException("The engine is not yet initialized");
		}
		return engine;
	}
	
	public String status() {
		return "server status is " + status;
	}

	public final void appctx(String path) {
		this.locals.put("appctx", path);
	}

	public final void assets(String path) {
		this.locals.put("assets", path);
	}

	public final void engine(String view) {
		try {
			switch (view) {
			case "jtwig":
				engine = TwigViewEngine.create(locals.getProperty("assets"), "html");
				break;
			case "freemarker":
				engine = FtlViewEngine.create(locals.getProperty("assets"), "ftl");
				break;
			case "string":
				engine = StringViewEngine.create(locals.getProperty("assets"), "js");
				break;
			default:
				throw new RuntimeException("specified engine not supported");
			}
			this.locals.put("engine", view);
		} catch (IOException e) {
			throw new RuntimeException("problem setting up view engine", e);
		}
	}

	public String resolve(String path) {
		String appctx = this.locals.getProperty("appctx");
		String path1 = !appctx.startsWith("/") ? "/" + appctx : appctx;
		if (path1.endsWith("/")) {
			path1 = path1.substring(0, path1.length() - 1);
		}
		String path2 = !path.startsWith("/") ? "/" + path : path;
		return path1 + path2;
	}

	public Set<String> locals() {
		return locals.stringPropertyNames();
	}

	public Object locals(String param) {
		return locals.get(param);
	}

	public AppServer lifecycle(String event, Consumer<String> callback) {
		this.lifecycle.subscribe(event, callback);
		return this;
	}

	public AppServer router() {
		this.routes = new AppRoutes(new MethodRouter());
		return this;
	}
	
	public AppServer router(Supplier<Router> supplier) {
		this.routes = new AppRoutes(supplier.get());
		return this;
	}

	public AppServer filter(HandlerFilter filter) {
		return filter("/*", filter);
	}

	public AppServer filter(String context, HandlerFilter filter) {
		FilterHolder holder = new FilterHolder(filter);
		servlets.addFilter(holder, context, EnumSet.of(DispatcherType.REQUEST));
		return this;
	}

	public AppServer route(String method, String path, HandlerServlet handler) {
		return route(method, path, null, handler);
	}
	
	public AppServer route(String method, String path, HandlerConfig config, HandlerServlet handler) {
		switch (method.toLowerCase()) {
		case "get":
			return get(path, "", "", config, handler);
		case "post":
			return post(path, "", "", config, handler);
		case "put":
			return put(path, "", "", config, handler);
		case "delete":
			return delete(path, "", "", config, handler);
		case "options":
			return options(path, "", "", config, handler);
		case "trace":
			return trace(path, "", "", config, handler);
		case "head":
			return head(path, "", "", config, handler);
		case "all":
			return all(path, "", "", config, handler);
		default:
			throw new UnsupportedOperationException(method + " is not a supported method");
		}
	}

	// ************* HEAD *****************//
	public AppServer head(String path, HandlerServlet handler) {
		return head(path, "", "", null, handler);
	}

	public AppServer head(String path, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return head(path, "", "", null, new HandlerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handle(HandlerRequest request, HandlerResponse response) {
				handler.apply(request, response);
			}
		});
	}

	public AppServer head(String path, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return head(path, "", "", config, handler);
	}

	public AppServer head(String path, String accept, String type, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return head(path, accept, type, config, new HandlerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handle(HandlerRequest request, HandlerResponse response) {
				handler.apply(request, response);
			}
		});
	}

	public AppServer head(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
		Route route = new Route(resolve(path), "head", accept, type);
		route.setId();
		routes.addRoute(route);
		// add servlet handler
		handler.setExecutor(threadPoolExecutor);
		ServletHolder holder = new ServletHolder(handler);
		if(config != null) config.configure(holder);
		servlets.addServlet(holder, route.rid);
		return this;
	}

	// ************* TRACE *****************//
	public AppServer trace(String path, HandlerServlet handler) {
		return trace(path, "", "", null, handler);
	}

	public AppServer trace(String path, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return trace(path, "", "", null, handler);
	}
	
	public AppServer trace(String path,  HandlerConfig config, HandlerServlet handler) {
		return trace(path, "", "", config, handler);
	}

	public AppServer trace(String path, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return trace(path, "", "", config, handler);
	}

	public AppServer trace(String path, String accept, String type, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return trace(path, accept, type, config, new HandlerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handle(HandlerRequest request, HandlerResponse response) {
				handler.apply(request, response);
			}
		});
	}

	public AppServer trace(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
		Route route = new Route(resolve(path), "trace", accept, type);
		route.setId();
		routes.addRoute(route);
		// add servlet handler
		handler.setExecutor(threadPoolExecutor);
		ServletHolder holder = new ServletHolder(handler);
		if(config != null) config.configure(holder);
		servlets.addServlet(holder, route.rid);
		return this;
	}

	// ************* OPTIONS *****************//
	public AppServer options(String path, HandlerServlet handler) {
		return options(path, "", "", null, handler);
	}

	public AppServer options(String path, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return options(path, "", "", null, handler);
	}
	
	public AppServer options(String path,  HandlerConfig config, HandlerServlet handler) {
		return options(path, "", "", config, handler);
	}

	public AppServer options(String path, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return options(path, "", "", config, handler);
	}

	public AppServer options(String path, String accept, String type, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return options(path, accept, type, config, new HandlerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handle(HandlerRequest request, HandlerResponse response) {
				handler.apply(request, response);
			}
		});
	}

	public AppServer options(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
		Route route = new Route(resolve(path), "options", accept, type);
		route.setId();
		routes.addRoute(route);
		// add servlet handler
		handler.setExecutor(threadPoolExecutor);
		ServletHolder holder = new ServletHolder(handler);
		if(config != null) config.configure(holder);
		servlets.addServlet(holder, route.rid);
		return this;
	}

	// ************* GET *****************//
	public AppServer get(String path, HandlerServlet handler) {
		return get(path, "", "", null, handler);
	}

	public AppServer get(String path, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return get(path, "", "", null, handler);
	}
	
	public AppServer get(String path,  HandlerConfig config, HandlerServlet handler) {
		return get(path, "", "", config, handler);
	}

	public AppServer get(String path, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return get(path, "", "", config, handler);
	}

	public AppServer get(String path, String accept, String type, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return get(path, accept, type, config, new HandlerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handle(HandlerRequest request, HandlerResponse response) {
				handler.apply(request, response);
			}
		});
	}

	public AppServer get(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
		Route route = new Route(resolve(path), "get", accept, type);
		route.setId();
		routes.addRoute(route);
		// add servlet handler
		handler.setExecutor(threadPoolExecutor);
		ServletHolder holder = new ServletHolder(handler);
		if(config != null) config.configure(holder);
		servlets.addServlet(holder, route.rid);
		return this;
	}

	// ************* POST *****************//
	public AppServer post(String path, HandlerServlet handler) {
		return post(path, "", "", null, handler);
	}

	public AppServer post(String path, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return post(path, "", "", null, handler);
	}
	
	public AppServer post(String path,  HandlerConfig config, HandlerServlet handler) {
		return post(path, "", "", config, handler);
	}

	public AppServer post(String path, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return post(path, "", "", config, handler);
	}

	public AppServer post(String path, String accept, String type, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return post(path, accept, type, config, new HandlerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handle(HandlerRequest request, HandlerResponse response) {
				handler.apply(request, response);
			}
		});
	}

	public AppServer post(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
		Route route = new Route(resolve(path), "post", accept, type);
		route.setId();
		routes.addRoute(route);
		// add servlet handler
		handler.setExecutor(threadPoolExecutor);
		ServletHolder holder = new ServletHolder(handler);
		// for multipart/form-data, customize the servlet holder
		if (type.toLowerCase().contains("multipart/form-data")) {
			MultipartConfigElement mpce = new MultipartConfigElement("temp", 1024 * 1024 * 50, 1024 * 1024, 5);
			holder.getRegistration().setMultipartConfig(mpce);
		}
		if(config != null) config.configure(holder);
		servlets.addServlet(holder, route.rid);
		return this;
	}

	// ************* PUT *****************//
	public AppServer put(String path, HandlerServlet handler) {
		return put(path, "", "", null, handler);
	}

	public AppServer put(String path, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return put(path, "", "", null, handler);
	}
	
	public AppServer out(String path,  HandlerConfig config, HandlerServlet handler) {
		return put(path, "", "", config, handler);
	}

	public AppServer put(String path, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return put(path, "", "", config, handler);
	}

	public AppServer put(String path, String accept, String type, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return put(path, accept, type, config, new HandlerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handle(HandlerRequest request, HandlerResponse response) {
				handler.apply(request, response);
			}
		});
	}

	public AppServer put(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
		Route route = new Route(resolve(path), "put", accept, type);
		route.setId();
		routes.addRoute(route);
		// add servlet handler
		handler.setExecutor(threadPoolExecutor);
		ServletHolder holder = new ServletHolder(handler);
		if(config != null) config.configure(holder);
		servlets.addServlet(holder, route.rid);
		return this;
	}

	// ************* DELETE *****************//
	public AppServer delete(String path, HandlerServlet handler) {
		return delete(path, "", "", null, handler);
	}

	public AppServer delete(String path, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return delete(path, "", "", null, handler);
	}
	
	public AppServer delete(String path,  HandlerConfig config, HandlerServlet handler) {
		return delete(path, "", "", config, handler);
	}

	public AppServer delete(String path, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return delete(path, "", "", config, handler);
	}

	public AppServer delete(String path, String accept, String type, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return delete(path, accept, type, config, new HandlerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handle(HandlerRequest request, HandlerResponse response) {
				handler.apply(request, response);
			}
		});
	}

	public AppServer delete(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
		Route route = new Route(resolve(path), "delete", accept, type);
		route.setId();
		routes.addRoute(route);
		// add servlet handler
		handler.setExecutor(threadPoolExecutor);
		ServletHolder holder = new ServletHolder(handler);
		if(config != null) config.configure(holder);
		servlets.addServlet(holder, route.rid);
		return this;
	}

	// ************* ALL *****************//
	public AppServer all(String path, HandlerServlet handler) {
		return all(path, "", "", null, handler);
	}

	public AppServer all(String path, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return all(path, "", "", null, handler);
	}
	
	public AppServer all(String path,  HandlerConfig config, HandlerServlet handler) {
		return all(path, "", "", config, handler);
	}

	public AppServer all(String path, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return all(path, "", "", config, handler);
	}

	public AppServer all(String path, String accept, String type, HandlerConfig config, BiFunction<HandlerRequest, HandlerResponse, Void> handler) {
		return all(path, accept, type, config, new HandlerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handle(HandlerRequest request, HandlerResponse response) {
				handler.apply(request, response);
			}
		});
	}

	public AppServer all(String path, String accept, String type, HandlerConfig config, HandlerServlet handler) {
		Route route = new Route(resolve(path), "*", accept, type);
		route.setId();
		routes.addRoute(route);
		// add servlet handler
		handler.setExecutor(threadPoolExecutor);
		ServletHolder holder = new ServletHolder(handler);
		if(config != null) config.configure(holder);
		servlets.addServlet(holder, route.rid);
		return this;
	}

	// ************* WEBSOCKETS *****************//
	public AppServer websocket(String ctx, AppWsProvider provider) {
		// Add a websocket to a specific path spec
		ServletHolder holderEvents = new ServletHolder("ws-events", new AppWsServlet(provider));
		servlets.addServlet(holderEvents, ctx);
		return this;
	}

	// ************* WORDPRESS *****************//
	public AppServer wordpress(String home, String fcgi_proxy) {
		this.wpcontext.put("activate", "true");
		this.wpcontext.put("resource_base", home);
		this.wpcontext.put("welcome_file", "index.php");
		this.wpcontext.put("fcgi_proxy", fcgi_proxy);
		this.wpcontext.put("script_root", home);
		return this;
	}

	// ************* START *****************//
	public void listen(int port, String host) {
		listen(port, host, null);
	}

	public void listen(int port, String host, Consumer<String> result) {
		try {
			status = "starting";
			// create server with thread pool
			QueuedThreadPool threadPool = new QueuedThreadPool(500, 5, 3000);
			server = new Server(threadPool);

			// Scheduler
			server.addBean(new ScheduledExecutorScheduler());

			// configure connector
			ServerConnector http = new ServerConnector(server);
			http.setHost(host);
			http.setPort(port);
			http.setIdleTimeout(3000);
			server.addConnector(http);
			
			// Setup JMX
			MBeanContainer mbeanContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
			server.addBean(mbeanContainer);
			
			// Setup ConnectorServer
			JMXServiceURL jmxURL = new JMXServiceURL("rmi", null, 1976, "/jndi/rmi:///jmxrmi");
			ConnectorServer jmxServer = new ConnectorServer(jmxURL, "org.eclipse.jetty.jmx:name=rmiconnectorserver");
			server.addBean(jmxServer);

			// TODO: configure secure connector
			// enable CORS
			FilterHolder corsFilter = new FilterHolder(CrossOriginFilter.class);
			corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
			corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM,
					"GET,POST,HEAD,PUT,TRACE,OPTIONS,DELETE");
			corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
					"X-Requested-With,Content-Type,Accept,Origin");
			corsFilter.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
			corsFilter.setInitParameter(CrossOriginFilter.PREFLIGHT_MAX_AGE_PARAM, "728000");
			corsFilter.setName("cross-origin");

			FilterMapping corsMapping = new FilterMapping();
			corsMapping.setFilterName("cross-origin");
			corsMapping.setPathSpec("*");
			servlets.addFilter(corsFilter, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
			// add routes filter
			servlets.addFilter(new FilterHolder(new ReRouteFilter(this.routes)), "/*", EnumSet.of(DispatcherType.REQUEST));

			// configure resource handlers
			String resourceBase = this.locals.getProperty("assets");

			// configure context for servlets
			String appctx = this.locals.getProperty("appctx");
			servlets.setContextPath(appctx);

			// configure default servlet for app context
			ServletHolder defaultServlet = createResourceServlet(resourceBase);
			servlets.addServlet(defaultServlet, "/*");
			
			// configure ResourceHandler to serve static files
			ResourceHandler appResources = createResourceHandler(resourceBase);

			// collect all context handlers
			ContextHandlerCollection contextHandlers = new ContextHandlerCollection();
			contextHandlers.addHandler(servlets);
			
			// add activated context handler (say, for php with fgci)
			if (Boolean.valueOf(this.wpcontext.get("activate"))) {
				contextHandlers.addHandler(create_fcgi_php(this.wpcontext));
			}

			// add handlers to the server
			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { appResources, contextHandlers, new DefaultHandler() });
			server.setHandler(handlers);

			// add shutdown hook
			addRuntimeShutdownHook(server);

			// start and access server using http://localhost:8080
			server.start();
			status = "running";
			if (result != null) {
				result.accept("AppServer is now listening on port " + port + "!");
			}
			server.join();
			status = "stopped";
		} catch (Throwable e) {
			e.printStackTrace(System.err);
			status = "stopped";
			result.accept("AppServer could not start because -> " + e.getMessage());
			System.exit(1);
		}
	}
	
	protected ThreadPoolExecutor createThreadPoolExecutor() {
		int poolSize = Integer.valueOf(this.locals.getProperty("poolSize", "100"));
		int maxPoolSize = Integer.valueOf(this.locals.getProperty("maxPoolSize", "200"));
		Long keepAliveTime = Long.valueOf(this.locals.getProperty("keepAliveTime", "5000"));
		return new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime,
				TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(poolSize));
	}

	protected ResourceHandler createResourceHandler(String resourceBase) {
		ResourceHandler appResources = new ResourceHandler();
		appResources.setDirectoriesListed(false);
		appResources.setWelcomeFiles(new String[] { "index.html" });
		appResources.setResourceBase(resourceBase);
		return appResources;
	}

	protected ServletHolder createResourceServlet(String resourceBase) {
		// DefaultServlet should be named 'default'
		ServletHolder defaultServlet = new ServletHolder("default", DefaultServlet.class);
		defaultServlet.setInitParameter("resourceBase", resourceBase);
		defaultServlet.setInitParameter("dirAllowed", "false");
		return defaultServlet;
	}

	protected ServletContextHandler create_fcgi_php(Map<String, String> phpctx) {
		ServletContextHandler php_ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
		php_ctx.setContextPath("/");
		php_ctx.setResourceBase(phpctx.get("resource_base"));
		php_ctx.setWelcomeFiles(new String[] { phpctx.get("welcome_file") });

		// add try filter
		FilterHolder tryHolder = new FilterHolder(new TryFilesFilter());
		tryHolder.setInitParameter("files", "$path /index.php?p=$path");
		php_ctx.addFilter(tryHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

		// Add default servlet (to serve the html/css/js)
		ServletHolder defHolder = new ServletHolder("default", new DefaultServlet());
		defHolder.setInitParameter("dirAllowed", "false");
		php_ctx.addServlet(defHolder, "/");

		// add fcgi servlet for php scripts
		ServletHolder fgciHolder = new ServletHolder("fcgi", new FastCGIProxyServlet());
		fgciHolder.setInitParameter("proxyTo", phpctx.get("fcgi_proxy"));
		fgciHolder.setInitParameter("prefix", "/");
		fgciHolder.setInitParameter("scriptRoot", phpctx.get("script_root"));
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

	class LifecyclePublisher implements LifeCycle.Listener {

		private final LifecycleSubscriber subscriber;

		public LifecyclePublisher(LifecycleSubscriber subscriber) {
			this.subscriber = subscriber;
		}

		@Override
		public void lifeCycleStarting(LifeCycle event) {
			subscriber.onStarting();
		}

		@Override
		public void lifeCycleStarted(LifeCycle event) {
			subscriber.onStarted();
		}

		@Override
		public void lifeCycleFailure(LifeCycle event, Throwable cause) {
			subscriber.onFailed(cause);
		}

		@Override
		public void lifeCycleStopping(LifeCycle event) {
			subscriber.onStopping();
		}

		@Override
		public void lifeCycleStopped(LifeCycle event) {
			subscriber.onStopped();
		}
	}

	class LifecycleSubscriber {

		private final Map<String, Consumer<String>> subscribers;
		private String[] stages = {"starting", "started", "stopping", "stopped", "failed"};

		public LifecycleSubscriber() {
			this.subscribers = new HashMap<>();
		}

		public void subscribe(String event, Consumer<String> callback) {
			if (subscribers.keySet().contains(event)) {
				this.subscribers.put(event, callback);
			} else {
				LOG.error("There is no such event as {}", event);
			}
		}

		public void onStarting() {
			subscribers.get("starting").accept(stages[0]);
		}

		public void onStarted() {
			subscribers.get("started").accept(stages[1]);
		}

		public void onStopping() {
			subscribers.get("stopping").accept(stages[2]);
		}

		public void onStopped() {
			subscribers.get("stopped").accept(stages[3]);
		}

		public void onFailed(Throwable thr) {
			subscribers.get("failed").accept(stages[4]);
		}
	}
}
