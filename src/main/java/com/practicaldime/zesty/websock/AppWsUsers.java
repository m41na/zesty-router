package com.practicaldime.zesty.websock;

import org.eclipse.jetty.websocket.api.Session;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AppWsUsers {

    public interface Searchable<S, E> {

        E search(S criteria);

        boolean contains(S criteria);

        int size();

        void add(E entity);

        void remove(E entity);
    }

    public static class ContextsNode implements Searchable<Search, UserId> {

        public final Map<String, TopicsNode> contexts = new ConcurrentHashMap<>();

        @Override
        public UserId search(Search criteria) {
            if (contexts.containsKey(criteria.context)) {
                return contexts.get(criteria.context).search(criteria);
            } else {
                throw new RuntimeException("could not find any context that matches '" + criteria.context + "'");
            }
        }

        @Override
        public boolean contains(Search criteria) {
            if (contexts.containsKey(criteria.context)) {
                return contexts.get(criteria.context).contains(criteria);
            }
            return false;
        }

        @Override
        public int size() {
            return contexts.size();
        }

        @Override
        public void add(UserId entity) {
            if (contexts.containsKey(entity.context)) {
                contexts.get(entity.context).add(entity);
            } else {
                TopicsNode newNode = new TopicsNode();
                newNode.add(entity);
                contexts.put(entity.context, newNode);
            }
        }

        @Override
        public void remove(UserId entity) {
            if (contexts.containsKey(entity.context)) {
                contexts.get(entity.context).remove(entity);
            } else {
                throw new RuntimeException("could not find any context that matches '" + entity.context + "'");
            }
        }
    }

    public static class TopicsNode implements Searchable<Search, UserId> {

        public final Map<String, UserIdsNode> topics = new ConcurrentHashMap<>();

        @Override
        public UserId search(Search criteria) {
            if (topics.containsKey(criteria.topic)) {
                return topics.get(criteria.topic).search(criteria);
            } else {
                throw new RuntimeException("could not find any topic that matches '" + criteria.topic + "'");
            }
        }

        @Override
        public boolean contains(Search criteria) {
            if (topics.containsKey(criteria.topic)) {
                return topics.get(criteria.topic).contains(criteria);
            }
            return false;
        }

        @Override
        public int size() {
            return topics.size();
        }

        @Override
        public void add(UserId entity) {
            if (topics.containsKey(entity.topic)) {
                topics.get(entity.topic).add(entity);
            } else {
                UserIdsNode newNode = new UserIdsNode();
                newNode.add(entity);
                topics.put(entity.topic, newNode);
            }
        }

        @Override
        public void remove(UserId entity) {
            if (topics.containsKey(entity.topic)) {
                topics.get(entity.topic).remove(entity);
            } else {
                throw new RuntimeException("could not find any topic that matches '" + entity.topic + "'");
            }
        }
    }

    public static class UserIdsNode implements Searchable<Search, UserId> {

        public final Map<String, UserId> users = new ConcurrentHashMap<>();

        @Override
        public UserId search(Search criteria) {
            if (users.containsKey(criteria.name)) {
                return users.get(criteria.name);
            } else {
                throw new RuntimeException("could not find any user with the name '" + criteria.name + "'");
            }
        }

        @Override
        public boolean contains(Search criteria) {
            return users.containsKey(criteria.name);
        }

        @Override
        public int size() {
            return users.size();
        }

        @Override
        public void add(UserId entity) {
            if (!users.containsKey(entity.name)) {
                users.put(entity.name, entity);
            }
        }

        @Override
        public void remove(UserId entity) {
            if (users.containsKey(entity.name)) {
                users.remove(entity);
            }
        }
    }

    public static class SearchableTree implements Searchable<Search, UserId> {

        private final ContextsNode nodes = new ContextsNode();

        @Override
        public UserId search(Search criteria) {
            return nodes.search(criteria);
        }

        @Override
        public boolean contains(Search criteria) {
            return nodes.contains(criteria);
        }

        @Override
        public int size() {
            return nodes.size();
        }

        @Override
        public void add(UserId entity) {
            nodes.add(entity);
        }

        @Override
        public void remove(UserId entity) {
            nodes.remove(entity);
        }
    }

    public static class Search {

        public final String name;
        public final String topic;
        public final String context;

        public Search(String name, String topic, String context) {
            this.name = name;
            this.topic = topic;
            this.context = context;
        }
    }

    public static class UserId implements Comparable<UserId> {

        public final String name;
        public final String topic;
        public final String context;
        public final Session session;

        public UserId(String name, String topic, String context, Session session) {
            this.name = name;
            this.topic = topic;
            this.context = context;
            this.session = session;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserId)) return false;
            UserId userId = (UserId) o;
            return name.equals(userId.name) &&
                    topic.equals(userId.topic) &&
                    context.equals(userId.context);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, topic, context);
        }

        @Override
        public int compareTo(UserId o) {
            if (this == o) return 0;
            int comparison = this.context.compareTo(o.context);
            if (comparison != 0) return comparison;
            comparison = this.topic.compareTo(o.topic);
            if (comparison != 0) return comparison;
            return this.name.compareTo(o.name);
        }

        @Override
        public String toString() {
            return "UserId{" +
                    "name='" + name + '\'' +
                    ", topic='" + topic + '\'' +
                    ", context='" + context + '\'' +
                    '}';
        }
    }
}
