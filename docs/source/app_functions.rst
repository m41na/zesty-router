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

