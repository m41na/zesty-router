Zesty-Router Docs
=================

Zesty is a creative JavaScript wrapper around the Jetty web server. It works out-of-the-box with the servlet 3.0 API which enables handling requests in asynchronous fashion.
Note that the servlet 3.1 API on the other hand, introduces new interfaces to handle asynchronous socket I/O for which there isn't an idiomatic way to handle generically 
in zesty yet. However, using either Javascript or Java with zesty is handled using a uniform and easy to understand API.

The App Object
^^^^^^^^^^^^^^

The is the heart and soul of the zesty framework. It holders references to objects that play a central role in how everthing else is held together.::

    * Properties locals;

This holds configuration attributes that can be passed to the application to alter its behavior.

    * ThreadPoolExecutor threadPoolExecutor;

This is a thread-pool that is seperate from the server's own thread pool. This is only used to handle requests for servlet marked as 'supports async'.
This thread pool is configurable using 3 parameters which are passed through the **locals** objects. These properties are:

    poolSize - the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
    maxPoolSize - he maximum number of threads to allow in the pool
    keepAliveTime when the number of threads is greater than the core, this is the maximum time (in MILLISECONDS) that excess idle threads will wait for new tasks before terminating.

    * Map<String, String> wpcontext;

If configuring the application for **wordpress**, this will holder the configuration parameters required.

    * ServletContextHandler servlets;

This is the servlet handlers' container for the application. All configured routes are added to this handler under one context.

    * ViewEngine engine;

This is an interface for exposing the active **view** component for the application.

The other configurations which can be passed through the **local** objects are:

    * assets - this points to the static resources folder
    * appctx - the base request path through which incoming requests will be handled
    * engine - the concrete view implementation to be used by the application. This could be either be

        * freemarker
        * jtwig
        * string
    * 



.. toctree::
   :maxdepth: 2
    
   license
   help


Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`
