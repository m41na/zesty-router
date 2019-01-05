App Functions
==============

These are the functions you can be able to use that are made accessible through the :code:`AppServer` instance.::

    let zesty = Java.type('com.practicaldime.zesty.app.AppProvider');

    let Date = Java.type('java.util.Date');
    let DateFormat = Java.type('java.text.SimpleDateFormat');

    function now() {
        return new DateFormat("hh:mm:ss a").format(new Date());
    }
    
    //create AppServer instance
    let app = zesty.provide({
        appctx: "/app",
        assets: "",
        engine: "freemarker"
    });

status() : String
^^^^^^^^^^^^^^^^^^

Return a value hinting at the health of the application::

    res.send(app.status().concat(" @ ").concat(now());

appctx(path: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Define the application's :code:`context path` through which requests should be routed. This value is held in the :code:`locals` object.

assets(path: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Define the directory, relative to the application's root directory, where :code:`static resources` will be located. This value is held in the :code:`locals` object.

engine(String view) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Define the application's view :code:`rendering engine` - valid values are currently either *freemarker* or *jtwig*. This value is held in the :code:`locals` object.

resolve(path: String) : String
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sanitizes the path value relative the :code:`appctx` to eliminate ambiguity when dealing with relativity of url paths.::

    res.redirect(app.resolve("/upload"));

locals() : Set<String>
^^^^^^^^^^^^^^^^^^^^^^^

Return the keys contained in the :code:`locals` object

locals(param: String) : Object
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Return a value from the :code:`locals` object using the given key

cors(params: Map) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Enable and provide request headers to configure *cors* for the application. If an empty or *null* map is provided, the application will use default values
to configure the *cors* filter, which are generously open. You can equally turn *cors* on/off through passing a *cors* true/false attribute to the 
:code:`locals` map.

router() : AppServer
^^^^^^^^^^^^^^^^^^^^^

Create the :code:`AppRoutes` object and return the current instance on :code:`AppServer` to allow method chaining.

router(supplier: Supplier) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This method matches :code:`router()`, except that is allows you to provide your own implementation of the :code:`Router` interface. 
This allows you to try out different algorithms for matching request handlers to the respective requests.

filter(filter: HandlerFilter) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Apply a filter at the front of the request processing pipeline where the outcome would either allow the request to proceed or send a response instead.
This filter is applied to all incoming requests

filter(context: String, filter: HandlerFilter) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Similar to :code:`filter(filter: HandlerFilter)` but this filter is only applied for requests on the specified context

route(method: String, path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Add a route to handle requests that match the specified *path* and *method*. This API is targeted for *Java* users.

route(method: String, path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Similar to the :code:`route(method: String, path: String, handler: HandlerServlet)`. However, this function allows you to 
dynamically configure the handler before it is added to the server. This is done through the :code:`HandlerConfig` interface.
A *Java* example is shown below.::
    
    get("/async/{value}", (holder)->holder.getRegistration().setAsyncSupported(true), new HandlerServlet() {
        @Override
        public void handle(HandlerRequest request, HandlerResponse response) {
            //code goes here
        }
    })

In this example, we configure the handler to use servlet 3.0's :code:`AsyncContext` object to process the request by setting 
:code:`setAsyncSupported` to *true*. This is how we leverage the :code:`AsyncContext` because the default value is *false*.

head(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *head* requests on the *path* url.

head(path: String, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *head* requests on the *path* url. This function is exactly like :code:`head(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

head(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *head* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

head(path: String, config: HandlerConfig, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *head* requests on the *path* url. This function is exactly like :code:`head(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

head(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *head* requests on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`head(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`head(...)` family eventually delegate to this function, which add the handler to the server.

trace(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *trace* requests on the *path* url.

trace(path: String, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *trace* requests on the *path* url. This function is exactly like :code:`trace(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

trace(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *trace* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

trace(path: String, config: HandlerConfig, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *trace* requests on the *path* url. This function is exactly like :code:`trace(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

trace(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *trace* requests on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`trace(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`trace(...)` family eventually delegate to this function, which add the handler to the server.

options(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *options* requests on the *path* url.

options(path: String, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *options* requests on the *path* url. This function is exactly like :code:`options(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

options(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *options* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

options(path: String, config: HandlerConfig, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *options* requests on the *path* url. This function is exactly like :code:`options(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

options(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *options* requests on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`options(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`options(...)` family eventually delegate to this function, which add the handler to the server.

get(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *get* requests on the *path* url.

get(path: String, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *get* requests on the *path* url. This function is exactly like :code:`get(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

get(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *get* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

get(path: String, config: HandlerConfig, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *get* requests on the *path* url. This function is exactly like :code:`get(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

get(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *get* requests on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`get(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`get(...)` family eventually delegate to this function, which add the handler to the server.

post(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *post* requests on the *path* url.

post(path: String, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *post* requests on the *path* url. This function is exactly like :code:`post(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

post(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *post* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

post(path: String, config: HandlerConfig, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *post* requests on the *path* url. This function is exactly like :code:`post(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

post(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *post* requests on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`post(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`post(...)` family eventually delegate to this function, which add the handler to the server.

put(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *put* requests on the *path* url.

put(path: String, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *put* requests on the *path* url. This function is exactly like :code:`put(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

put(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *put* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

put(path: String, config: HandlerConfig, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *put* requests on the *path* url. This function is exactly like :code:`put(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

put(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *put* requests on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`put(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`put(...)` family eventually delegate to this function, which add the handler to the server.

delete(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *delete* requests on the *path* url.

delete(path: String, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *delete* requests on the *path* url. This function is exactly like :code:`delete(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

delete(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *delete* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

delete(path: String, config: HandlerConfig, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *delete* requests on the *path* url. This function is exactly like :code:`delete(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

delete(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *delete* requests on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`delete(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`delete(...)` family eventually delegate to this function, which add the handler to the server.

all(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle requests of any *method* on the *path* url.

all(path: String, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle requests of any *method* on the *path* url. This function is exactly like :code:`all(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

all(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle requests of any *method* on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

all(path: String, config: HandlerConfig, handler: BiFunction) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle requests of any *method* on the *path* url. This function is exactly like :code:`all(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

all(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle requests of any *method* on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`all(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`all(...)` family eventually delegate to this function, which add the handler to the server.

websocket(ctx: String, provider: AppWsProvider) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a websocket handler for the application. This will handle websocket requests on the :code:`ctx` context path. The :code:`AppWsProvider` object
should provide a prepared instance of :code:`WebSocketAdapter`. You can use :code:`AppWebSocket` which is the the default implementation provided by the framework.

wordpress(home: String, fcgi_proxy: String) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure an :code:`FCGI` handler to use with any application that *speaks* fcgi like :code:`Wordpress`.

listen(port: int, host: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Fire up the server and start listening for requests on the specified *host* and on the specified *port*.

listen(port: int, host: String, result: Consumer) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This is similar to :code:`listen(port: int, host: String)` except that is also accepts a consumer which gets invoked when the start up is completed. 

lifecycle(event: String, callback: Consumer) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Add a lifecycle listener which gets invoked when the corresponding lifecycle even happens. The lifecycle events that will trigger the listener are:

    * starting  - happens when the server begins to get instantiated
    * started   - happens when the server is started successfully
    * stopping  - happens when the server begins to shut down
    * stopped   - happens when the server has stopped running
    * failed    - happens when the server is unable to start successfully
