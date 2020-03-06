package com.practicaldime.router.core.handler;

public enum HttpStatusCode  {

    //Information responses
    Continue(100, "Continue", "Interim response indicating that everything so far is OK and that the client should continue the request, or ignore the response if the request is already finished."),
    Switching_Protocol(101, "Switching Protocol", "Sent in response to an Upgrade request header from the client, and indicates the protocol the server is switching to."),
    Processing(102, "Processing", "Indicates that the server has received and is processing the request, but no response is available yet"),
    Early_Hints(103, "Early Hints", "Intended to be used with the Link header, letting the user agent start preloading resources while the server prepares a response."),

    //Successful responses
    Ok(200, "OK", "Request has succeeded. The meaning of the success depends on the HTTP method"),
    Created(201, "Created", "Request has succeeded and a new resource has been created as a result. This is typically the response sent after POST requests, or some PUT requests"),
    Accepted(202, "Accepted", "Request has been received but not yet acted upon."),
    Non_Authoritative_Information(203, "Non-Authoritative Information", "Response code means the returned meta-information is not exactly the same as is available from the origin server, but is collected from a local or a third-party copy"),
    No_Content(204, "No Content", "There is no content to send for this request, but the headers may be useful."),
    Reset_Content(205, "Reset Content", "Tells the user-agent to reset the document which sent this request."),
    Partial_Content(206, "Partial Content", "Response code is used when the Range header is sent from the client to request only part of a resource."),
    Multi_Status(207, "Multi-Status", "Conveys information about multiple resources, for situations where multiple status codes might be appropriate."),
    Already_Reported(208, "Already Reported", ""),
    IM_Used(226, "IM Used", "The server has fulfilled a GET request for the resource, and the response is a representation of the result of one or more instance-manipulations applied to the current instance."),

    //Permanent re-directions
    Moved_Permanently(301, "Moved Permanently", "Reorganization of a Web site."),
    Permanent_Redirect(308, "Permanent Redirect", "Reorganization of a Web site, with non-GET links/operations."),
    //Temporary re-directions
    Found(302, "Found", "The Web page is temporarily unavailable for unforeseen reasons."),
    See_Other(303, "See Other", "Used to redirect after a PUT or a POST, so that refreshing the result page doesn't re-trigger the operation."),
    Temporary_Redirect(307, "Temporary Redirect", "The Web page is temporarily unavailable for unforeseen reasons. Better than 302 when non-GET operations are available on the site."),
    //Special re-directions
    Multiple_Choice(300, "Multiple Choice", "Not many: the choices are listed in an HTML page in the body. Machine-readable choices are encouraged to be sent as Link headers with rel=alternate."),
    Not_Modified(304, "Not Modified", "Sent for revalidated conditional requests. Indicates that the cached response is still fresh and can be used."),

    //Client error responses
    Bad_Request(400, "Bad Request", ""),
    Unauthorized(401, "Unauthorized", "Although the HTTP standard specifies \"unauthorized\", semantically this response means \"unauthenticated\". That is, the client must authenticate itself to get the requested response."),
    Payment_Required(402, "Payment Required", "Response code is reserved for future use."),
    Forbidden(403, "Forbidden", "The client does not have access rights to the content; that is, it is unauthorized, so the server is refusing to give the requested resource. Unlike 401, the client's identity is known to the server."),
    Not_Found(404, "Not Found", "The server can not find the requested resource. In the browser, this means the URL is not recognized. In an API, this can also mean that the endpoint is valid but the resource itself does not exist."),
    Method_Not_Allowed(405, "Method Not Allowed", "The request method is known by the server but has been disabled and cannot be used."),
    Not_Acceptable(406, "Not Acceptable", "This response is sent when the web server, after performing server-driven content negotiation, doesn't find any content that conforms to the criteria given by the user agent."),
    Proxy_Authentication_Required(407, "Proxy Authentication Required", ""),
    Request_Timeout(408, "Request Timeout", "This response is sent on an idle connection by some servers, even without any previous request by the client. It means that the server would like to shut down this unused connection."),
    Conflict(409, "Conflict", "This response is sent when a request conflicts with the current state of the server"),
    Gone(410, "Gone", "This response is sent when the requested content has been permanently deleted from server, with no forwarding address. Clients are expected to remove their caches and links to the resource. "),
    Length_Required(411, " Length Required", "Server rejected the request because the Content-Length header field is not defined and the server requires it."),
    Precondition_Failed(412, "Precondition Failed", "The client has indicated preconditions in its headers which the server does not meet."),
    Payload_Too_Large(413, "Payload Too Large", "Request entity is larger than limits defined by server; the server might close the connection or return an Retry-After header field."),
    URI_Too_Long(414, "URI Too Long", "The URI requested by the client is longer than the server is willing to interpret."),
    Unsupported_Media_Type(415, "Unsupported Media Type", "The media format of the requested data is not supported by the server, so the server is rejecting the request."),
    Range_Not_Satisfiable(416, "Range Not Satisfiable", "The range specified by the Range header field in the request can't be fulfilled; it's possible that the range is outside the size of the target URI's data."),

    //Server error responses
    Internal_Server_Error(500, "Internal Server Error", "The server has encountered a situation it doesn't know how to handle."),
    Not_Implemented(501, "Not Implemented", "The request method is not supported by the server and cannot be handled. The only methods that servers are required to support (and therefore that must not return this code) are GET and HEAD."),
    Bad_Gateway(502, "Bad Gateway", "This error response means that the server, while working as a gateway to get a response needed to handle the request, got an invalid response."),
    Service_Unavailable(503, "Service Unavailable", "The server is not ready to handle the request. Common causes are a server that is down for maintenance or that is overloaded."),
    Gateway_Timeout(504, "Gateway Timeout", "This error response is given when the server is acting as a gateway and cannot get a response in time."),
    HTTP_Version_Not_Supported(505, "HTTP Version Not Supported", "The HTTP version used in the request is not supported by the server.");

    public final int code;
    public final String text;
    public final String usage;

    HttpStatusCode(int code, String text, String usage) {
        this.code = code;
        this.text = text;
        this.usage = usage;
    }
}
