package com.practicaldime.zesty.router;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathRegexRouter implements Routing.Router {

    public static final String CHAR_ENCODING = "UTF-8";
    private Map<PathPattern, Routing.Router> routers = new HashMap<>();

    public static void main(String... args) {
        String regex = "\\/todo\\/(\\{.+?\\})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher("/todo/{id}");
        System.out.println(matcher.find());

        String value = "/todo/({.+?})";
        System.out.println(value.replaceAll("\\{(.+?, replacement)\\}", "\\\\{$1\\\\}").replaceAll("\\/", "\\\\/"));
    }

    @Override
    public void search(Routing.Search input) {
        for (PathPattern pathRegex : routers.keySet()) {
            Pattern valuesPattern = pathRegex.valuesPattern;
            Matcher matcher = valuesPattern.matcher(input.attributes.url);
            if (matcher.matches()) {
                routers.get(pathRegex).search(input);
                //if a matching route is found, extract path params from the input's url and get the values path params if any
                if (input.result != null) {
                    if (matcher.groupCount() > 0) {
                        Pattern paramsPattern = pathRegex.paramsPattern;
                        Matcher routeMatcher = paramsPattern.matcher(input.result.path);
                        if (routeMatcher.matches()) {
                            for (int i = 1; i <= matcher.groupCount(); i++) {
                                String paramVal = null;
                                try {
                                    paramVal = URLDecoder.decode(matcher.group(i), CHAR_ENCODING);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace(System.err);
                                }
                                String paramKey = routeMatcher.group(i);
                                if (paramKey != null) {
                                    input.pathParams.put(paramKey, Optional.ofNullable(paramVal).orElse(""));
                                }
                            }
                        } else {
                            throw new RuntimeException("Expected paramsPattern does not match on input path");
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean contains(Routing.Search criteria) {
        for (PathPattern pathRegex : routers.keySet()) {
            Pattern valuesPattern = pathRegex.valuesPattern;
            Matcher matcher = valuesPattern.matcher(criteria.attributes.url);
            if (matcher.matches()) {
                return routers.get(pathRegex).contains(criteria);
            }
        }
        return false;
    }

    @Override
    public void info(List<String> nodes, String prefix) {
        String indent = prefix + "|-";
        routers.entrySet().stream().forEach(entry -> entry.getValue().info(nodes, indent));
    }

    @Override
    public void add(Routing.Route route) {
        String valuesRegex = route.path.replaceAll("\\{(.+?)\\}", "(.+?)").replaceAll("\\/", "\\\\/");
        String paramsRegex = route.path.replaceAll("\\{(.+?)\\}", "\\\\{(.+?)\\\\}").replaceAll("\\/", "\\\\/");
        PathPattern pathRegex = new PathPattern(valuesRegex, paramsRegex);
        if (!routers.containsKey(pathRegex)) {
            routers.put(pathRegex, new HeadersRouter());
        }
        routers.get(pathRegex).add(route);
    }

    @Override
    public void remove(Routing.Route entity) {
        for (PathPattern pathRegex : routers.keySet()) {
            Pattern valuesPattern = pathRegex.valuesPattern;
            Matcher matcher = valuesPattern.matcher(entity.path);
            if (matcher.matches()) {
                routers.get(pathRegex).remove(entity);
                break;
            }
        }
    }

    public static class PathPattern implements Comparable<PathPattern> {

        public final Pattern valuesPattern;
        public final Pattern paramsPattern;
        private final String valuesRegex;
        private final String paramsRegex;

        public PathPattern(String valuesRegex, String paramsRegex) {
            super();
            this.valuesRegex = valuesRegex;
            this.paramsRegex = paramsRegex;
            this.valuesPattern = Pattern.compile(valuesRegex);
            this.paramsPattern = Pattern.compile(paramsRegex);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((paramsRegex == null) ? 0 : paramsRegex.hashCode());
            result = prime * result + ((valuesRegex == null) ? 0 : valuesRegex.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PathPattern other = (PathPattern) obj;
            if (paramsRegex == null) {
                if (other.paramsRegex != null)
                    return false;
            } else if (!paramsRegex.equals(other.paramsRegex))
                return false;
            if (valuesRegex == null) {
                return other.valuesRegex == null;
            } else return valuesRegex.equals(other.valuesRegex);
        }

        @Override
        public int compareTo(PathPattern that) {
            if (that == null) return 1;
            if (this == that) return 0;
            return this.paramsRegex.compareTo(that.paramsRegex);
        }
    }
}
