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

Add a key-value property in the *locals* object.

assets(path: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Set the application's context through which requests can be routed

engine(String view) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Set the application's view rendring engine - valid values are currently either *freemarker* or *jtwig*.

resolve(path: String) : String
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sanitizes the path value relative the :code:`appctx` to eliminate ambiguity when searching for files.::

    res.redirect(app.resolve("/upload"));

locals() : Set<String>
^^^^^^^^^^^^^^^^^^^^^^^

Return the keys contained in the :code:`locals` object

locals(param: String) : Object
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Return a value from the :code:`locals` object using the given key

router() : AppServer
^^^^^^^^^^^^^^^^^^^^^

Create the :code:`AppRoutes` object object and return the current instance on :code:`AppServer`.

filter(filter: HandlerFilter) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Apply a filter at the front of the request processing pipeline where the outcome would allow the request to proceed or send a response instead.
This filter is applied to all incoming requests

filter(context: String, filter: HandlerFilter) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Similar to :code:`filter(filter: HandlerFilter)` but this filter is only applied for requests on the specified context

route(method: String, path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Add a route to handle request that match the specified *path* and *method*. This API is targeted for *Java* users.

route(method: String, path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Similar to the :code:`route(method: String, path: String, handler: HandlerServlet)`. However, this function allows you to 
dynamically configure the servlet before it is added to the server. This is done through the :code:`HandlerConfig` interface.
A *Java* example is shown below.::
    
    get("/async/{value}", (holder)->holder.getRegistration().setAsyncSupported(true), new HandlerServlet() {
        @Override
        public void handle(HandlerRequest request, HandlerResponse response) {
            //code goes here
        }
    })

In this example, we configure the handler to use servlet 3.0's :code:`AsyncContext` object to process the request by setting 
:code:`setAsyncSupported` to *true*. This is how we leverage the :code:`AsyncContext` becasue the default value is *false*.

head(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *head* requests on the *path* url.

head(path: String, handler: BiFunction<HttpServletRequest, HttpServletResponse, Void>) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *head* requests on the *path* url. This function is exactly like :code:`head(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

head(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *head* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

head(path: String, config: HandlerConfig, handler: BiFunction<HttpServletRequest, HttpServletResponse, Void>) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

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

trace(path: String, handler: BiFunction<HttpServletRequest, HttpServletResponse, Void>) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *trace* requests on the *path* url. This function is exactly like :code:`trace(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

trace(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *trace* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

trace(path: String, config: HandlerConfig, handler: BiFunction<HttpServletRequest, HttpServletResponse, Void>) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *trace* requests on the *path* url. This function is exactly like :code:`trace(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

trace(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *trace* requests on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`trace(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`trace(...)` family eventually delegate to this function, which add the handler to the server.

options(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *options* requests on the *path* url.

options(path: String, handler: BiFunction<HttpServletRequest, HttpServletResponse, Void>) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *options* requests on the *path* url. This function is exactly like :code:`options(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

options(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *options* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

options(path: String, config: HandlerConfig, handler: BiFunction<HttpServletRequest, HttpServletResponse, Void>) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *options* requests on the *path* url. This function is exactly like :code:`options(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

options(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *options* requests on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`options(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`options(...)` family eventually delegate to this function, which add the handler to the server.

get(path: String, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *get* requests on the *path* url.

get(path: String, handler: BiFunction<HttpServletRequest, HttpServletResponse, Void>) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *get* requests on the *path* url. This function is exactly like :code:`get(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

get(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *get* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

get(path: String, config: HandlerConfig, handler: BiFunction<HttpServletRequest, HttpServletResponse, Void>) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

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

post(path: String, handler: BiFunction<HttpServletRequest, HttpServletResponse, Void>) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configures a route to handle a *post* requests on the *path* url. This function is exactly like :code:`post(path, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

post(path: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *post* requests on the *path* url. This function also uses the :code:`HandlerConfig` config object
to further customize the handler before it is added to the server. With the :code:`config.getRegistration()` object, the handler
can be further customized with servlet specific properties to adapt to different requirements.

post(path: String, config: HandlerConfig, handler: BiFunction<HttpServletRequest, HttpServletResponse, Void>) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *post* requests on the *path* url. This function is exactly like :code:`post(path, config, handler: HandlerServlet)`
and is more favorable for use with Javascript instead of Java.

post(path: String, accept: String, type: String, config: HandlerConfig, handler: HandlerServlet) : AppServer
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Configure a route to handle a *post* requests on the *path* url, with a *content-type* of header of *type* value and *accept* header of
*accept* value. This function is exactly like :code:`post(path, config, handler: HandlerServlet)` with the added parameters. All the other
functions in the :code:`post(...)` family eventually delegate to this function, which add the handler to the server.

::

    **Please check again soon. More material coming soon**