HandlerRequest Object
=======================

The HandlerRequest object is a wrapper around the HttpServletRequest object. Besides proving access to the entire HttpServletRequest API, 
the HandlerRequest object also adds some convenient methods which abstract the mundane and arduous tasks for performing some pretty common chores.

protocol() : String
^^^^^^^^^^^^^^^^^^^^

Wraps around HttpServletRequest's getProtocol method

secure() : boolean
^^^^^^^^^^^^^^^^^^^

Returns true if the protocol is https

hostname() : String
^^^^^^^^^^^^^^^^^^^^

Wraps around HttpServletRequest's getRemoteHost method

ip() : String
^^^^^^^^^^^^^^

Wraps around HttpServletRequest's getRemoteAddr method

path() : String
^^^^^^^^^^^^^^^^

Wraps around HttpServletRequest's getRequestURI method

route() : RouteSearch
^^^^^^^^^^^^^^^^^^^^^^

Returns the route matched together with path parameters discovered as a Map<String, String> pathParams and the 
RequestAttrs used to search for the route.

param(name: String) : String
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Returns a named path parameter extracted from the request uri. This is defined using {curly braces} in the request path mapping.

pathParams() : Map
^^^^^^^^^^^^^^^^^^^

Returns a map of all named path parameters extracted from the request uri. These are defined using *{curly braces}* in the request path mapping.

query() : String
^^^^^^^^^^^^^^^^^

Wraps around HttpServletRequest's getQueryString method

header(name: String) : String
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Wraps around HttpServletRequest's getHeader(name) method

error() : boolean
^^^^^^^^^^^^^^^^^^

Return an error status associated with the request if something went wrong and was captured

message() : String
^^^^^^^^^^^^^^^^^^^

Return a message associated with the request if one was saved when the request was processed.

body() : byte[]
^^^^^^^^^^^^^^^^

Return the request body captured if the long capture() was called prior.

body(type: Class) : T 
^^^^^^^^^^^^^^^^^^^^^^

Used to return the request's body() content as either json or xml depending on the respective Content-Type header in the request.

body(provider: BodyReader) : T
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Used to return the request's body() content that is transformed using the BodyReader implementation supplied by caller of this method.

capture() : long
^^^^^^^^^^^^^^^^^

Captures the request body into a byte[] which is subsequently accessible using the byte[] body() method.

upload(destination: String) : long
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Used together with a servlet designated for uploading multipart/form-data using the POST method and that is configured with a MultipartConfigElement object. The configured values for (String location, long maxFileSize, long maxRequestSize, int fileSizeThreshold) are
("temp", 1024 * 1024 * 50, 1024 * 1024, 5) respectively.::

    router.get('/upload', function (req, res) {
        res.render('upload', {}); //return upload page
    });

    router.post('/upload', '', 'multipart/form-data', function (req, res) {
        var dest = req.param('destination');
        req.upload(dest);
        res.redirect(app.resolve("/upload")); //redirect to upload page
    });

And the equivalent Java syntax would be.::

    get("/upload", (HandlerRequest request, HandlerResponse response) -> {
        response.render("upload", Maps.newHashMap());
    });

    post("/www/upload", "", "multipart/form-data", (HandlerRequest request, HandlerResponse response) ->{
        String dest = request.param("destination");
        request.upload(dest);
        response.redirect(app.resolve("/upload"));
    });

cookies() : Cookie[]
^^^^^^^^^^^^^^^^^^^^^

Returns the cookies associated with the request.

session(create: boolean) : HttpSession
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Returns the session object associated with the request. If the create attribute is true, a new session is created and returned. If it is false it will only return a session if one already exists otherwise it will not create one and returns nothing.

