package com.practicaldime.zesty.route;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class AppRoutes {

    private static final Logger LOG = LoggerFactory.getLogger(AppRoutes.class);
    private static AppRoutes instance;
    private final Set<AppRoute> routes = new HashSet<>();
    private final Gson gson = new Gson();

    private AppRoutes() {
        super();
    }

    public static AppRoutes instance() {
        if (instance == null) {
            synchronized (AppRoutes.class) {
                instance = new AppRoutes();
            }
        }
        return instance;
    }

    public void addRoute(AppRoute route) {
    	LOG.debug("adding route {}", gson.toJson(route));
        this.routes.add(route);
    }

    public Set<AppRoute> getRoutes() {
        return this.routes;
    }

    public AppRoute search(HttpServletRequest input) {
        String path = input.getRequestURI();
        String method = input.getMethod();
        String accepts = input.getHeader("Accept");
        String contentType = input.getHeader("Content-Type");
        AppRoute search = new AppRoute(path, method, accepts, contentType);
        for(Enumeration<String> keys = input.getHeaderNames(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            search.headers.put(key, input.getHeader(key));
        }
        return match(search);
    }

    public AppRoute match(AppRoute search) {
        for (AppRoute route : routes) {
            String method = search.method;
            if(!"*".equals(route.method)){
                if (isNotBlank(route.method) && isNotBlank(method)) {
                    if (!method.toLowerCase().equals(route.method.toLowerCase())) {
                        continue;
                    }
                }
            }
            String accepts = search.accept;
            if (isNotBlank(route.accept) && isNotBlank(accepts)) {
                if (!accepts.contains(route.accept)) {
                    continue;
                }
            }
            String contentType = search.contentType;
            if (isNotBlank(route.contentType) && isNotBlank(contentType)) {
                if (!contentType.contains(route.contentType)) {
                    continue;
                }
            }
            if (route.headers.size() > 0) {
                boolean contains = true;
                for (String key : route.headers.keySet()) {
                    String inputHeader = search.headers.get(key);
                    if (inputHeader == null || !inputHeader.contains(route.headers.get(key))) {
                        contains = false;
                        break;
                    }
                }
                if (!contains) {
                    continue;
                }
            }
            String path = search.path;
            if (route.path != null && path != null) {
                if (pathMatch(route, path)) {
                    return route;
                }
            }
        }
        return null;
    }

    /* Example values
    * routePath - /book/:isbn/author/:name
    * inputPath - /book/12345/author/steve
    * 
    *step 1. identify param patterns in the configured route (:.*?)[\b\W]
    *step 2. replace the patterns identified with literals (:.*?)
    *step 3. resulting string from step 3 becomes regex to match agains input uri
    *step 4. match identified params from route
     */
    private boolean pathMatch(AppRoute route, String inputPath) {
        Map<String, String> params = route.pathParams;
        //steps 1,2,3
        String paramRegex = "((:\\w+)(\\W|\\b)??)";
        String inputRegex = route.path.replaceAll(paramRegex, "(\\\\w+?)");
        Pattern inputPattern = Pattern.compile("^" + inputRegex + "$");        
        //steps 4
        Pattern paramPattern = Pattern.compile(paramRegex);
        Matcher paramMatcher = paramPattern.matcher(route.path);
        LOG.info("derived route pattern [{}] from route [{}] to match against input [{}]", inputPattern.pattern(), route, inputPath);
        
        Matcher inputMatcher = inputPattern.matcher(inputPath);
        if (inputMatcher.matches()) {
            int index = 1;
            while (paramMatcher.find()) {
                String param = paramMatcher.group(1);
                String value = inputMatcher.group(index++);
                params.put(param, value);
            }
            LOG.info("the input [{}] matched the pattern [{}]", inputPath, inputPattern.pattern());
            return true;
        }
        return false;
    }
    
    private boolean isNotBlank(String value) {
    	return value != null && value.trim().length() > 0;
    }
}
