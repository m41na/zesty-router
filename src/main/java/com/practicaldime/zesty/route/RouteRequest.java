package com.practicaldime.zesty.route;

import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

public interface RouteRequest {
    
    String protocol();
    
    boolean secure();
    
    String hostname();
    
    String ip();
    
    String path();
    
    String param(String name);
    
    String query();
    
    String header(String name);
    
    boolean error();
    
    String message();
    
    long upload(String dest);
    
    long capture();
    
    byte[] body();
    
    <T>T body(Class<T> type);
    
    <T>T body(BodyReader<T> provider);
    
    Cookie[] cookies();
    
    AppRoute route();
    
    void route(AppRoute route);
    
    HttpSession session(boolean create);
    
    Map<String, String> pathParams();
}
