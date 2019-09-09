package com.practicaldime.zesty.router;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Route {

    public final String path;
    public final String accept;
    public final String contentType;
    public final Map<String, String> headers = new HashMap<>();
    public String method;
    public String rid;

    public Route(String rid, String path, String method, String accept, String contentType, Map<String, String> headers) {
        this.rid = rid;
        this.path = path;
        this.method = method;
        this.accept = accept;
        this.contentType = contentType;
        this.headers.putAll(headers);
    }

    public Route(String... args) {
        this(null,
                args.length > 0 ? args[0] : null,
                args.length > 1 ? args[1] : null,
                args.length > 2 ? args[2] : null,
                args.length > 3 ? args[3] : null,
                Collections.emptyMap());
    }

    public Route(String path, String method, String accept, String contentType) {
        this(null,
                path,
                method,
                accept,
                contentType,
                Collections.emptyMap());
    }

    public void setId() {
        this.rid = "/" + UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "Route [path=" + path + ", method=" + method + ", accept=" + accept + ", contentType="
                + contentType + ", headers=" + headers + "]";
    }
}
