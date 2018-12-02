package com.practicaldime.zesty.route;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppRoute {
    
    public String pathId;
    public final String path;
    public final String method;
    public final String accept;
    public final String contentType;
    public final Map<String, String> headers = new HashMap<>();
    public final Map<String, String> pathParams = new HashMap<>();
    
    public AppRoute(String...args){
        this.path =  args.length > 0? args[0] : null;
        this.method = args.length > 1? args[1] : null;
        this.accept = args.length > 2? args[2] : null;
        this.contentType = args.length > 3? args[3] : null;
    }

    public AppRoute(String path, String method, String accept, String contentType) {
        this.path = path;
        this.method = method;
        this.accept = accept;
        this.contentType = contentType;
    }
    
    public void setId(){
        this.pathId = "/" + UUID.randomUUID().toString();
    }
}
