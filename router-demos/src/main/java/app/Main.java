package app;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.router.core.config.HandlerConfig;
import com.practicaldime.router.core.server.IServer;
import com.practicaldime.router.core.server.Rest;
import com.practicaldime.router.core.sse.EventsEmitter;
import com.practicaldime.router.http.app.AppServer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.eclipse.jetty.servlets.EventSource;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {

    public static void main(String[] args) {
        int test = 3;
        switch (test) {
            case 0:
                restTest(args);
                break;
            case 1:
                snapTest();
                break;
            case 2:
                syncJdbcTest();
                break;
            case 3:
                syncBatchJdbcTest();
                break;
            default:
                asyncBatchJdbcTest();
                break;
        }
    }

    public static void syncJdbcTest() {
        PGRepo repo = new PGRepo();
        int count = 0;
        int testSize = 1000;
        int batchSize = 100;
        Long startTime = System.nanoTime();
        for (int i = 0; i < testSize; i++) {
            Integer res = repo.addTodo("task_" + i);
            count += res;
            if (i % batchSize == 0) {
                System.out.println(count + " items inserted in batch " + i / 100);
                Long duration = System.nanoTime() - startTime;
                System.out.println("Took " + duration / 10e6 + " milli-seconds");
            }
        }
        Long duration = System.nanoTime() - startTime;
        System.out.println("Took " + duration / 10e6 + " milli-seconds");
        repo.clearTodos();
    }

    public static void syncBatchJdbcTest() {
        PGRepo repo = new PGRepo();
        int testSize = 100000;
        int batchSize = 1000;
        String[] todos = new String[testSize];
        for (int i = 0; i < testSize; i++) {
            todos[i] = "task__" + i;
        }
        Long startTime = System.nanoTime();
        int created = repo.addTodos(todos, batchSize);
        Long duration = System.nanoTime() - startTime;
        System.out.println("Took " + duration / 10e6 + " milli-seconds to create " + created + " items");
        repo.clearTodos();
    }

    public static void asyncBatchJdbcTest() {
        PGRepo repo = new PGRepo();
        int testSize = 100000;
        int batchSize = 1000;
        String[] todos = new String[testSize];
        for (int i = 0; i < testSize; i++) {
            todos[i] = "task__" + i;
        }
        Long startTime = System.nanoTime();
        repo.addTodosAsync(todos, batchSize).handle((res, th) -> {
            if (th == null) {
                System.out.println("inserted " + res + " items");
                return res;
            } else {
                System.out.println(th.getMessage());
                return 0;
            }
        }).thenAccept(created -> {
            Long duration = System.nanoTime() - startTime;
            System.out.println("Took " + duration / 10e6 + " milli-seconds to create " + created + " items");
            repo.clearTodos();
        }).join();
        System.out.println("insert request completed");
    }

    public static void snapTest() {
        PGRepo repo = new PGRepo();
        Integer res = repo.addTodo("buy milk");
        if (res > 0) {
            System.out.println("new record created");
            List<Task> tasks = repo.fetchTodos(0, 10);
            System.out.println("Items count = " + tasks.size());
            if (tasks.size() > 5) {
                tasks.forEach(t -> {
                    Task task = repo.fetchTodo(t.id);
                    System.out.println("attempt to delete " + task);
                    Integer dropped = repo.dropTodo(t.id);
                    if (dropped > 0) {
                        System.out.println("dropped task with id '" + t.id + "'");
                    }
                });
            }
        }
    }

    public static void restTest(String[] args) {

        Repo repo = new Repo();
        Rest rest = new Rest() {

            HandlerConfig config = cfg -> cfg.setAsyncSupported(true);

            @Override
            public IServer provide(Map<String, String> properties) {
                return AppServer.instance(properties);
            }

            @Override
            public Function<Map<String, String>, IServer> build(IServer app) {
                return (props) -> app.assets("/", "www")
                        .get("/todo", (req, res, done) -> {
                            res.json(repo.list());
                            done.complete();
                        })
                        .get("/todo/{name}", (req, res, done) -> {
                            String name = req.param("id");
                            res.json(repo.fetch(name));
                            done.complete();
                        })
                        .post("/todo/{name}", "application/json", "application/json", config, (req, res, done) -> {
                            String task = req.param("name");
                            repo.add(task);
                            res.accepted();
                            done.complete();
                        })
                        .put("/todo/{name}", "application/json", "application/json", config, (req, res, done) -> {
                            String task = req.param("name");
                            repo.complete(task);
                            res.accepted();
                            done.complete();
                        })
                        .delete("/todo/{name}", (req, res, done) -> {
                            String task = req.param("name");
                            repo.remove(task);
                            res.accepted();
                            done.complete();
                        })
                        .subscribe("/todos", config, new EventsEmitter() {

                            Disposable subscription;

                            @Override
                            public void onOpen(ObjectMapper mapper, EventSource.Emitter emitter) {
                                repo.subject.subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(Disposable disposable) {
                                        subscription = disposable;
                                        try {
                                            emitter.event("subscribed", "subscription has been accepted");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNext(TaskEvent taskEvent) {
                                        try {
                                            emitter.data(mapper.writeValueAsString(taskEvent));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        try {
                                            emitter.event("error", throwable.getMessage());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            subscription.dispose();
                                        }
                                    }

                                    @Override
                                    public void onComplete() {
                                        try {
                                            emitter.event("completed", "subscription has been completed");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            subscription.dispose();
                                        }
                                    }
                                });
                            }
                        });
            }
        };

        rest.start(args);
    }

    @JsonAutoDetect
    static class Task {

        public Long id;
        public String name;
        public Boolean completed;

        @JsonCreator
        public Task(@JsonProperty("id") Long id, @JsonProperty("name") String name, @JsonProperty("completed") Boolean completed) {
            this.id = id;
            this.name = name;
            this.completed = completed;
        }

        @Override
        public String toString() {
            return "Task{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", completed=" + completed +
                    '}';
        }
    }

    @JsonAutoDetect
    static class TaskEvent {

        public Task task;
        public Type event;

        @JsonCreator
        public TaskEvent(@JsonProperty("task") Task task, @JsonProperty("type") Type event) {
            this.task = task;
            this.event = event;
        }

        enum Type {CREATED, UPDATED, REMOVED}
    }

    static class Repo {

        Map<String, Task> tasks = new HashMap<>();
        AtomicLong ids = new AtomicLong(1);
        BehaviorSubject<TaskEvent> subject = BehaviorSubject.create();

        void add(String name) {
            Task task = new Task(ids.getAndIncrement(), name, false);
            tasks.put(task.name, task);
            //publish task created
            subject.onNext(new TaskEvent(task, TaskEvent.Type.CREATED));
        }

        void remove(String name) {
            Task task = tasks.remove(name);
            //publish task removed
            subject.onNext(new TaskEvent(task, TaskEvent.Type.REMOVED));
        }

        Task fetch(String name) {
            return tasks.get(name);
        }

        void complete(String name) {
            for (Task task : tasks.values()) {
                if (task.name.equalsIgnoreCase(name)) {
                    task.completed = !task.completed;
                    //publish task updated
                    subject.onNext(new TaskEvent(task, TaskEvent.Type.UPDATED));
                    break;
                }
            }
        }

        List<Task> list() {
            return new LinkedList<>(tasks.values());
        }
    }

    static class PGRepo {

        public final String DB_URL = "jdbc:postgresql://localhost/";
        public final String DB_DRIVER = "org.postgresql.Driver";
        public final String DB_USER = "postgres";
        public final String DB_PASS = "admins";
        public final String CREATE_TODOS_TABLE = "" +
                "CREATE TABLE IF NOT EXISTS tbl_todos( " +
                "id SERIAL, " +
                "name varchar(32) not null, " +
                "completed boolean not null default false, " +
                "primary key (id) " +
                ")";
        public final String insertTodo = "insert into tbl_todos (name) values (?)";
        public final String updateTodo = "update tbl_todos set name=?, completed=? where id=?";
        public final String deleteTodo = "delete from tbl_todos where id=?";
        public final String selectTodo = "select id, name, completed from tbl_todos where id=?";
        public final String fetchTodos = "select id, name, completed from tbl_todos offset ? limit ?";
        public final String clearTodos = "truncate table tbl_todos";

        public PGRepo() {
            try {
                init();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private final Supplier<Connection> pool() {
            PoolProperties p = new PoolProperties();
            p.setUrl(DB_URL);
            p.setDriverClassName(DB_DRIVER);
            p.setUsername(DB_USER);
            p.setPassword(DB_PASS);
            p.setJmxEnabled(true);
            DataSource datasource = new DataSource();
            datasource.setPoolProperties(p);
            return () -> {
                try {
                    return datasource.getConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            };
        }

        private void init() throws ClassNotFoundException, SQLException {
            Class.forName(DB_DRIVER);
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                System.out.println("Connecting to database...");
                Statement stmt = conn.createStatement();
                int res = stmt.executeUpdate(CREATE_TODOS_TABLE);
                if (res > 0) {
                    System.out.println("Table created");
                }
            }
        }

        public Integer addTodo(String todo) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                try (PreparedStatement pst = conn.prepareStatement(insertTodo)) {
                    pst.setString(1, todo);
                    return pst.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Could not insert new todo '" + todo + "' item");
                    e.printStackTrace();
                    return 0;
                }
            } catch (SQLException e) {
                System.out.println("Could not connect to database...");
                e.printStackTrace();
                return null;
            }
        }

        public Integer addTodos(String[] todos, int batchSize) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                try (PreparedStatement pst = conn.prepareStatement(insertTodo)) {
                    Integer count = 0;
                    for (int i = 0; i < todos.length; i += batchSize) {
                        //create batch
                        for (int j = 0; j < batchSize && (i + j) < todos.length; j++) {
                            pst.setString(1, todos[i + j]);
                            pst.addBatch();
                        }
                        pst.clearParameters();
                        int[] batched = pst.executeBatch();
                        count += batched.length;
                        System.out.println("batch size: " + batchSize + ". batch success: " + batched.length);
                    }
                    pst.clearBatch();
                    return count;
                } catch (SQLException e) {
                    System.err.println("Could not insert new todos batch");
                    e.printStackTrace();
                    return 0;
                }
            } catch (SQLException e) {
                System.out.println("Could not connect to database...");
                e.printStackTrace();
                return null;
            }
        }

        public CompletableFuture<Integer> addTodosAsync(String[] todos, int batchSize) {
            return CompletableFuture.supplyAsync(() -> {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    try (PreparedStatement pst = conn.prepareStatement(insertTodo)) {
                        Integer count = 0;
                        for (int i = 0; i < todos.length; i += batchSize) {
                            //create batch
                            for (int j = 0; j < batchSize && (i + j) < todos.length; j++) {
                                pst.setString(1, todos[i + j]);
                                pst.addBatch();
                            }
                            pst.clearParameters();
                            int[] batched = pst.executeBatch();
                            count += batched.length;
                            System.out.println("batch size: " + batchSize + ". batch success: " + batched.length);
                        }
                        pst.clearBatch();
                        return count;
                    } catch (SQLException e) {
                        System.err.println("Could not insert new todos batch");
                        e.printStackTrace();
                        return 0;
                    }
                } catch (SQLException e) {
                    System.out.println("Could not connect to database...");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }

        public Task fetchTodo(Long id) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                try (PreparedStatement pst = conn.prepareStatement(selectTodo)) {
                    pst.setLong(1, id);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            return new Task(rs.getLong("id"), rs.getString("name"), rs.getBoolean("completed"));
                        }
                        return null;
                    }
                } catch (SQLException e) {
                    System.err.println("Could not find todo item by id'" + id + "'");
                    e.printStackTrace();
                    return null;
                }
            } catch (SQLException e) {
                System.out.println("Could not connect to database...");
                e.printStackTrace();
                return null;
            }
        }

        public List<Task> fetchTodos(Integer offset, Integer limit) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                try (PreparedStatement pst = conn.prepareStatement(fetchTodos)) {
                    pst.setInt(1, offset);
                    pst.setInt(2, limit);
                    try (ResultSet rs = pst.executeQuery()) {
                        List<Task> tasks = new LinkedList<>();
                        while (rs.next()) {
                            tasks.add(new Task(rs.getLong("id"), rs.getString("name"), rs.getBoolean("completed")));
                        }
                        return tasks;
                    }
                } catch (SQLException e) {
                    System.err.println("Could not fetch todo items from offset '" + offset + "'");
                    e.printStackTrace();
                    return null;
                }
            } catch (SQLException e) {
                System.out.println("Could not connect to database...");
                e.printStackTrace();
                return null;
            }
        }

        public Integer dropTodo(Long id) {
            try (Connection conn = pool().get()) {
                try (PreparedStatement pst = conn.prepareStatement(deleteTodo)) {
                    pst.setLong(1, id);
                    return pst.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Could not remove todo by id '" + id + "'");
                    e.printStackTrace();
                    return 0;
                }
            } catch (SQLException e) {
                System.out.println("Could not connect to database...");
                e.printStackTrace();
                return null;
            }
        }

        public Integer clearTodos() {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                try (Statement pst = conn.createStatement()) {
                    return pst.executeUpdate(clearTodos);
                } catch (SQLException e) {
                    System.err.println("Could not clear todos from db");
                    e.printStackTrace();
                    return 0;
                }
            } catch (SQLException e) {
                System.out.println("Could not connect to database...");
                e.printStackTrace();
                return null;
            }
        }
    }
}
