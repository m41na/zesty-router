package com.practicaldime.zesty.router;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public class RouteBuilder {

    private String rid;
    private String path;
    private String method;
    private String accept;
    private String contentType;
    private Map<String, String> headers;

    private RouteBuilder() {
    }

    public static RouteBuilder newRoute() {
        return new RouteBuilder();
    }

    public static Route create(String rid, String path, String method, String accept, String contentType, Map<String, String> headers) {
        return newRoute().routeId(rid).path(path).method(method).accept(accept).contentType(contentType).headers(() -> headers).build();
    }

    public static Route create(String rid, String path, String method, String accept, String contentType) {
        return create(rid, path, method, accept, contentType, Collections.emptyMap());
    }

    public static Route create(String rid, String path, String method, String accept) {
        return create(rid, path, method, accept, null, Collections.emptyMap());
    }

    public static Route create(String rid, String path, String method) {
        return create(rid, path, method, null, null, Collections.emptyMap());
    }

    public RouteBuilder routeId(String rid) {
        this.rid = rid;
        return this;
    }

    public RouteBuilder path(String path) {
        this.path = path;
        return this;
    }

    public RouteBuilder method(String method) {
        this.method = method;
        return this;
    }

    public RouteBuilder accept(String accept) {
        this.accept = accept;
        return this;
    }

    public RouteBuilder contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public RouteBuilder headers(Supplier<Map<String, String>> supplier) {
        this.headers = supplier.get();
        return this;
    }

    public Route build() {
        return new Route(rid,
                path,
                method,
                accept = accept != null && accept.trim().length() > 0 ? accept : null,
                contentType != null && contentType.trim().length() > 0 ? contentType : null,
                headers);
    }
}
