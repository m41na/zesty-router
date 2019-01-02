AppServer Object
=====================

The is one of the core components in the zesty framework. It holds references to objects that play a central role in how everthing else is held together.::

    public AppServer() {
        this(new HashMap<>());
    }

    public AppServer(Map<String, String> props) {
        this.assets(Optional.ofNullable(props.get("assets")).orElse("www"));
        this.appctx(Optional.ofNullable(props.get("appctx")).orElse("/"));
        this.engine(Optional.ofNullable(props.get("engine")).orElse("jtwig"));
        this.threadPoolExecutor = createThreadPoolExecutor();
    }

locals: Properties
^^^^^^^^^^^^^^^^^^^

This holds configuration attributes that can be passed to the application and that are used by other components internally to configure their own behavior.

threadPoolExecutor: ThreadPoolExecutor
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This is a thread-pool that is separate from the server's own thread pool. This is only used to handle requests for handlers configured as '*supports async*'.
This thread pool is configurable using 3 parameters which are passed through the **locals** objects. These properties are:

    * *poolSize* - the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
    * *maxPoolSize* - the maximum number of threads to allow in the pool
    * *keepAliveTime* - when the number of threads is greater than the core, this is the maximum time (in MILLISECONDS) that excess idle threads will wait for new tasks before terminating.

wpcontext: Map
^^^^^^^^^^^^^^^

If configuring the application for :code:`wordpress`, this will hold the configuration parameters required.::

    // ************* WORDPRESS *****************//
    public AppServer wordpress(String home, String fcgi_proxy) {
        this.wpcontext.put("activate", "true");
        this.wpcontext.put("resource_base", home);
        this.wpcontext.put("welcome_file", "index.php");
        this.wpcontext.put("fcgi_proxy", fcgi_proxy);
        this.wpcontext.put("script_root", home);
        return this;
    }

servlets: ServletContextHandler
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This is the servlet handlers' container for the application. All configured routes are added to this handler under one context path.

engine: ViewEngine
^^^^^^^^^^^^^^^^^^^

This is an interface for exposing the active **view** component to the application. The value it holds specifies the concrete view 
implementation to be used by the application. This could be either be *freemarker*, *jtwig* at the moment.

AppProvider Object
===================

This is a provider for the **AppServer** object which is comes in handy when working with Javascript.::

    public class AppProvider {

        public static AppServer provide(Map<String, String> props) {
            return new AppServer(props);
        }
    }

You can alternatively just simply instantiate the **AppServer** manually.

HandlerConfig Object
=====================

This interface allows you to dynamically customize the request handler (the underlying object is a :code:`HttpServletRequest`) with the help of 
:code:`ServletRegistration.Dynamic` to suit specific requirements.::

    public interface HandlerConfig {

        public void configure(ServletHolder holder);
    }

Through the :code:`holder.getRegistration()` object, you will have access to servlet-specific methods like:

    * setLoadOnStartup(int loadOnStartup)
    * setMultipartConfig(MultipartConfigElement multipartConfig)
    * Set<String> addMapping(String... urlPatterns)
    * setAsyncSupported(boolean supported)

among others.

BodyWriter Object
==================

This interface allows you to take over the response generation responsibility and the output is sent back to the client as the response body. ::

    public interface BodyWriter<T> {
        
        byte[] transform(T object);
    }

BodyReader Object
==================

This interface allows you to take over the request body processing responsibility and the output is used by the application to process the request. ::
    
    public interface BodyReader<T> {
        
        T transform(String type, byte[] bytes);
    }

