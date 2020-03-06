package com.practicaldime.router.http.examples;

import com.practicaldime.router.core.server.IServer;
import com.practicaldime.router.core.server.Rest;
import com.practicaldime.router.http.app.AppServer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class SimpleRest {

    public static void main(String... args) {
        UserDao dao = new UserDao();

        Rest rest = new Rest() {

            @Override
            public IServer provide(Map<String, String> properties) {
                properties.put("appctx", "/users");
                properties.put("port", "8080");
                return AppServer.instance(properties);
            }

            @Override
            public Function<IServer, IServer> compose() {
                return app ->
                        app.get("/", (handler) -> handler.setAsyncSupported(true), (req, res, done) -> {
                            res.json(dao.all());
                            done.complete();
                        })
                                .get("/{id}", (req, res, done) -> {
                                    String id = req.param("id");
                                    User result = dao.findById(Integer.valueOf(id));
                                    res.json(result);
                                    done.complete();
                                })
                                .get("/email/{email}", (req, res, done) -> {
                                    String email = req.param("email");
                                    res.json(dao.findByEmail(email));
                                    done.complete();
                                })
                                .post("/create", (req, res, done) -> {
                                    String name = req.param("name");
                                    String email = req.param("email");
                                    dao.save(name, email);
                                    res.status(201);
                                    done.complete();
                                })
                                .put("/update/{id}", (req, res, done) -> {
                                    String id = req.param("id");
                                    String name = req.param("name");
                                    String email = req.param("email");
                                    dao.update(Integer.valueOf(id), name, email);
                                    res.status(204);
                                    done.complete();
                                })
                                .delete("/delete/{id}", (req, res, done) -> {
                                    String id = req.param("id");
                                    dao.delete(Integer.valueOf(id));
                                    res.status(205);
                                    done.complete();
                                })
                                .put("/error", (req, res, done) -> {
                                    throw new UnsupportedOperationException("bad things happened will happen");
                                });
            }
        };

        rest.start(args);
    }

    static class User {

        private int id;
        private String name;
        private String email;

        public User(String name, String email, int id) {
            super();
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    static class UserDao {

        private AtomicInteger lastId;
        private Map<Integer, User> users = new HashMap<>();

        public UserDao() {
            users.put(0, new User("James", "james@jjs.io", 0));
            users.put(1, new User("Steve", "steve@jjs.io", 1));
            users.put(2, new User("Carol", "carol@jjs.io", 2));
            users.put(3, new User("Becky", "becky@jjs.io", 3));
            lastId = new AtomicInteger(users.size() - 1);
        }

        public Map<Integer, User> all() {
            return this.users;
        }

        public void save(String name, String email) {
            int id = lastId.incrementAndGet();
            users.put(id, new User(name, email, id));
        }

        public User findById(int id) {
            return this.users.get(id);
        }

        public User findByEmail(String email) {
            return users.values().stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst()
                    .orElse(null);
        }

        public void update(int id, String name, String email) {
            users.put(id, new User(name, email, id));
        }

        public void delete(int id) {
            users.remove(id);
        }
    }
}
