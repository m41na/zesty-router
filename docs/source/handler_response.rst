HandlerResponse Object
======================

The HandlerResponse object is a wrapper around the HttpServletResponse object. Besides proving access to the entire 
HttpServletResponse API, the HandlerResponse object also adds some convenient methods which abstract the mundane and 
arduous tasks for performing some pretty common chores.

header(header: String, value: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Wraps around HttpServletResponse's setHeader method

templates(folder: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sets the directory name where the configured template engine will look up files

context(ctx: String): void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sets the contextPath value in the request to that configured in the application level

status(status: int) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Wraps around HttpServletResponse's :code:`setStatus(status)` method

sendStatus(status: int) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Set the response status through status(int status) and the sends the status message as the response body

end(payload: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sends the payload in UTF-8 format as the response body. Example usage.::

    let Date = Java.type('java.util.Date');
    let DateFormat = Java.type('java.text.SimpleDateFormat');
    function now() {
        return new DateFormat("hh:mm:ss a").format(new Date());
    }
    router.get('/', function (req, res) {
        res.send(app.status().concat(" @ ").concat(now()));
    });

json(payload: Object) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sends the payload in UTF-8 format and content-type as *application/json* as the response body

jsonp(payload: Object) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sends the payload in UTF-8 format and content-type as *application/json* as the response body

xml(payload: Object, template: Class<T>) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sends the payload in UTF-8 format and content-type as *application/json* as the response body. 
The template variable is used to determine the structure of the xml payload

content(payload: <T>, writer: BodyWriter<T>) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sends the payload transformed by the writer object as the response body. The writer object is supplied by the caller of this method

render(template: String, model: Map<String, Object>) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Delegate the task of writing the response to the configured template engine. The engine will locate and load the template and merge it 
with the model object to create the response body

next(path: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^

Sets the forward flag to true, and sets the target uri to the path value prior to calling the request dispatcher to forward the request to the target resource.

redirect(path: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sets the redirect flag to true, sets the target uri to the path value and sets the response status to 303 (see other) prior to calling the 
request to redirect to the target resource. Example usage.::

    router.get('/target', function (req, res) {
        res.redirect(app.resolve("/dest/target"));
    });

redirect(status: int, path: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sets the redirect flag to true, sets the target uri to the path value and sets the response status to status prior to calling the request 
to redirect to the target resource. A redirect could be done using either 301, 302 or 302 depending on your context, so this redirect 
method gives you that discretion.

type(mimetype: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Wraps around HttpServletResponse's setContentType method

cookie(name: String, value: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Wraps around HttpServletResponse's addCookie method

attachment(filename: String) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Wraps around the download method and provides default values for attributes as shown: 
:code:`download(filename, filename, getContentType(), null)`;

download(path: String, filename: String, mimeType: String, status: HandlerStatus) : void
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Reads the specified file relative the the application's root, and writes it to the response as a raw byte stream. An optional HandlerStatus callback interface is used to communicate progress as the file gets written

getContent() : byte[]
^^^^^^^^^^^^^^^^^^^^^^

Return the byte[] content stored in the response object