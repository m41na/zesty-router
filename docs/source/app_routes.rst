AppRoutes Object
=================

This object holds the routes configured in the application to *route* requests to their respective handlers.::

    public AppServer router() {
        this.routes = new AppRoutes(new MethodRouter());
        return this;
    }

The AppRoutes object implements the Router which provides two methods - one for adding routes and the other for route lookup. 
It uses a tree structure and has a lookup efficiency equivalent to the height of the tree. The AppRoutes object itself holds 
the root of the tree. Each node in the tree implements the same Router interface.::

    public interface Router {
        void accept(RouteSearch input);    
        void addRoute(Route route);
    }

The actual mapped routes are located in the leaf nodes, which makes the lookup time equivalent to the tree height. 
The nodes at each level are used to literally route the lookup down the correct path to the target route which matches the request.
The root node is a method router - **GET, POST, PUT & DELETE**. It will drive the lookup down to the correct branch.::

    public AppServer router() {
        this.routes = new AppRoutes(new MethodRouter());
        return this;
    }

Each node accepts a RouteSearch objects which represents the incoming request, and uses this to determine which node it should pass the search object to.::

    public void accept(RouteSearch input) {
        String method = input.requestAttrs.method;
        Method type = method != null? Method.valueOf(method.toUpperCase()) : null;
        if(type != null) {
            this.routers.get(type).accept(input);
            //if a matching route is found, set the method value in the result
            if(input.requestAttrs != null) {
                input.result.method = type.name();
            }
        }
    }

With a successful lookup, the :code:`if(input.requestAttrs != null)` is not null, and each Router adds necessary information to the RouteSearch object as the search call stack unwinds.
The method router node contains PathPartsRouter routers. These will route the lookup depending on the length of the path split along '/' (the path separator) character.::

    public void accept(RouteSearch input) {
        String inputPath = input.requestAttrs.url;
        String path = inputPath != null? (inputPath.startsWith("/")? inputPath.substring(1) : inputPath) : null;
        String[] parts = path != null? path.split("/") : null;
        if(parts != null) {
            Integer length = Integer.valueOf(parts.length);
            if(routers.containsKey(length)) {
                routers.get(length).accept(input);
            }
        }
    }

The PathPartsRouter router node contains PathRegexRouter routers. These will route the lookup depending on the match found between the mapped routes and the request input represented by the RouteSearch object.::

    public void accept(RouteSearch input) {
        for(PathPattern pathRegex : routers.keySet()) {
            Pattern valuesPattern = pathRegex.valuesPattern;
            Matcher matcher = valuesPattern.matcher(input.requestAttrs.url);
            if(matcher.matches()) {
                ...
            }
        }
    }

If there is a match found, the request is routed to the HeadersRouter. The HeadersRouter is a leaf node. This will pull out the matching route based on the headers in the mapped route.::

    public void accept(RouteSearch input) {
        List pool = new ArrayList<>(routes);
        for(Iterator iter = pool.iterator(); iter.hasNext();) {
            Route mapping = iter.next();
            //is 'content-type' declared in mapping route?
            if(mapping.contentType != null && mapping.contentType.trim().length() > 0) {
                ...
            }
            ...
            input.result = pool.size() > 0? pool.get(0) : null;
        }
    }

At this point, the RouteSearch will either have the result property assigned a matched route of set to null, and the call stack begins to unwind back to the root node.
The void addRoute(Route route); in each router is called by the AppServer when accepting routes mapped by a user. Each path taken to place the route node in the correct place is the same path which is used to look up the node based on a request, which makes the logic simpler to understand.


