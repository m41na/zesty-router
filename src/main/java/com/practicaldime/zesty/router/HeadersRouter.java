package com.practicaldime.zesty.router;

import java.util.*;

public class HeadersRouter implements Routing.Router {

    private List<Routing.Route> routes = new ArrayList<>();

    @Override
    public void search(Routing.Search input) {
        List<Routing.Route> pool = new ArrayList<>(routes);
        for (Iterator<Routing.Route> iter = pool.iterator(); iter.hasNext(); ) {
            Routing.Route mapping = iter.next();
            //is 'content-type' declared in mapping route?
            String contentTypeHeader = Optional.ofNullable(input.attributes.getHeader("Content-Type"))
                    .orElse(input.attributes.getHeader("content-type"));
            if (mapping.contentType != null && mapping.contentType.trim().length() > 0) {
                if (mapping.contentType.equals("*")) {
                    continue;
                } else if (contentTypeHeader != null) {
                    if (!contentTypeHeader.contains(mapping.contentType)) {
                        iter.remove();
                        continue;
                    }
                } else {
                    iter.remove();
                    continue;
                }
            } else if (mapping.contentType != null && mapping.contentType.equals("")) {
                continue;
            } else if (contentTypeHeader != null && contentTypeHeader.trim().length() > 0) {
                iter.remove();
                continue;
            }
            //is 'accept' declared in mapping route?
            String acceptHeader = Optional.ofNullable(input.attributes.getHeader("Accept"))
                    .orElse(input.attributes.getHeader("accept"));
            if (mapping.accept != null && mapping.accept.trim().length() > 0) {
                if (mapping.accept.equals("*")) {
                    continue;
                } else if (acceptHeader != null) {
                    if (!acceptHeader.contains(mapping.accept)) {
                        iter.remove();
                        continue;
                    }
                } else {
                    iter.remove();
                    continue;
                }
            } else if (mapping.accept != null && mapping.accept.equals("")) {
                continue;
            } else if (acceptHeader != null && acceptHeader.trim().length() > 0) {
                iter.remove();
                continue;
            }
            //are there headers in the mapping route that match the inputs?
            if (!mapping.headers.isEmpty()) {
                Map<String, String> headers = mapping.headers;
                for (String key : headers.keySet()) {
                    if (input.attributes.headers.containsKey(key)) {
                        if (!input.attributes.getHeader(key).contains(headers.get(key))) {
                            iter.remove();
                            break;
                        }
                    }
                }
            }
        }
        //set first matched route if it exists in the input object
        if (pool.size() > 1) {
            throw new RuntimeException("Matched more than one route. You need more specificity with these matched routes -> " + pool.toString());
        } else if (pool.size() == 0) {
            throw new RuntimeException("No match was matched for against incoming inputs -> " + input.attributes.toString());
        } else {
            input.result = pool.get(0);
        }
    }

    /**
     * return true on the first route that matches on 'accept' and 'content-type' headers, else false
     *
     * @param criteria
     * @return
     */
    @Override
    public boolean contains(Routing.Search criteria) {
        for (Iterator<Routing.Route> iter = routes.iterator(); iter.hasNext(); ) {
            Routing.Route next = iter.next();
            if (next.contentType.equalsIgnoreCase(criteria.attributes.getHeader("content-type")) &&
                    next.accept.equalsIgnoreCase(criteria.attributes.getHeader("accept"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void info(List<String> nodes, String prefix) {
        String indent = prefix + "|-";
        routes.stream().forEach(route -> nodes.add(indent + route.toString()));
    }

    @Override
    public void add(Routing.Route route) {
        this.routes.add(route);
    }

    /**
     * removes first route that matches on 'accept' and 'content-type' headers
     *
     * @param entity
     */
    @Override
    public void remove(Routing.Route entity) {
        for (Iterator<Routing.Route> iter = routes.iterator(); iter.hasNext(); ) {
            Routing.Route next = iter.next();
            if (next.contentType.equalsIgnoreCase(entity.contentType) &&
                    next.accept.equalsIgnoreCase(entity.accept)) {
                iter.remove();
                break;
            }
        }
    }
}
