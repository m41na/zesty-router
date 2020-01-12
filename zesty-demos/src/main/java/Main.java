import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.servlet.HandlerConfig;
import com.practicaldime.zesty.sse.EventsEmitter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import org.apache.commons.cli.Options;
import org.eclipse.jetty.servlets.EventSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.practicaldime.zesty.app.AppOptions.applyDefaults;

public class Main {

    public static void main(String[] args) {

        Repo repo = new Repo();

        HandlerConfig config = cfg -> cfg.setAsyncSupported(true);
        Map<String, String> props = applyDefaults(new Options(), args);
        AppServer.instance(props).assets("/", "www")
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
                })
                .listen(8888, "localhost", System.out::println);
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
}
