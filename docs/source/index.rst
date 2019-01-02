Zesty-Router Docs
=================

Zesty is a creative JavaScript wrapper around the Jetty web server. It works out-of-the-box with the servlet 3.0 API which enables handling requests in asynchronous fashion.
Note that the servlet 3.1 API on the other hand, introduces new interfaces to handle asynchronous socket I/O for which there isn't an idiomatic way to handle generically 
in zesty yet. However, using either Javascript or Java with zesty is handled using a uniform and easy to understand API.

The AppServer Object
^^^^^^^^^^^^^^^^^^^^^

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

locals: Properties;
-------------------

This holds configuration attributes that can be passed to the application and that are used by other components internally to configure their own behavior.

threadPoolExecutor: ThreadPoolExecutor;
---------------------------------------

This is a thread-pool that is seperate from the server's own thread pool. This is only used to handle requests for servlet marked as '*supports async*'.
This thread pool is configurable using 3 parameters which are passed through the **locals** objects. These properties are:

    * *poolSize* - the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
    * *maxPoolSize* - he maximum number of threads to allow in the pool
    * *keepAliveTime* - when the number of threads is greater than the core, this is the maximum time (in MILLISECONDS) that excess idle threads will wait for new tasks before terminating.

wpcontext: Map<String, String>;
-------------------------------

If configuring the application for **wordpress**, this will hold the configuration parameters required.::

    // ************* WORDPRESS *****************//
    public AppServer wordpress(String home, String fcgi_proxy) {
        this.wpcontext.put("activate", "true");
        this.wpcontext.put("resource_base", home);
        this.wpcontext.put("welcome_file", "index.php");
        this.wpcontext.put("fcgi_proxy", fcgi_proxy);
        this.wpcontext.put("script_root", home);
        return this;
    }

servlets: ServletContextHandler;
--------------------------------

This is the servlet handlers' container for the application. All configured routes are added to this handler under one context.

engine: ViewEngine;
-------------------

This is an interface for exposing the active **view** component for the application. The other configurations which can be passed through 
the **local** objects are:

    * assets - this points to the static resources folder
    * appctx - the base request path through which incoming requests will be handled
    * engine - the concrete view implementation to be used by the application. This could be either be *freemarker*, *jtwig* or *string*.

AppProvider Object
^^^^^^^^^^^^^^^^^^

This is a provider for the **AppServer** object which is comes in handy when working with Javascript.::

    public class AppProvider {

        public static AppServer provide(Map<String, String> props) {
            return new AppServer(props);
        }
    }

You can alternatively just simply instantiate the **AppServer** manually.

.. toctree::
   :maxdepth: 3

   app_routes
   handler_request
   handler_response
   app_functions
   license
   help


Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`
