package com.practicaldime.router.core.handler;

import com.practicaldime.router.core.routing.Routing;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.Map;

public interface RouteRequest {

    String protocol();

    boolean secure();

    String hostname();

    String ip();

    String path();

    String param(String name);

    <T>T param(String name, Class<T> type);

    Map<String, String> pathParams();

    String query();

    String header(String name);

    <T> T attribute(String name, Class<T> type);

    boolean error();

    String message();

    long upload(String dest);

    long capture();

    byte[] body();

    <T> T body(Class<T> type);

    <T> T body(BodyReader<T> provider);

    Cookie[] cookies();

    Routing.Search route();

    void route(Routing.Search route);

    HttpSession session(boolean create);
}
