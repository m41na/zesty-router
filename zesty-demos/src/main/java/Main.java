import com.google.common.collect.LinkedListMultimap;
import com.practicaldime.zesty.app.AppServer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    public static void main(String[] args){

        Repo repo = new Repo();

        new AppServer().router()
        .get("/todo", (req, res, done) -> {
            res.json(repo.tasks);
            done.complete();
        })
        .post("/todo/{name}", "application/json", "application/json", null, (req, res, done) -> {
            String task = req.param("name");
            repo.add(task);
            done.complete();
        })
        .put("/todo/{name}", "application/json", "application/json", null, (req, res, done) -> {
            String task = req.param("name");
            repo.complete(task);
            done.complete();
        })
        .delete("/todo/{name}", (req, res, done) -> {
            String task = req.param("name");
            repo.remove(task);
            done.complete();
        })
//        .subscribe("/todo", done -> {
//
//        })
        .listen(8888, "localhost", System.out::println);
    }

    static class Task{

        Long id;
        String name;
        Boolean completed;

        public Task(Long id, String name, Boolean completed) {
            this.id = id;
            this.name = name;
            this.completed = completed;
        }
    }

    static class Repo {

        Map<String, Task> tasks = new HashMap<>();
        AtomicLong ids = new AtomicLong(1);

        void add(String name){
            Task task = new Task(ids.getAndIncrement(), name, false);
            tasks.put(task.name, task);
        }

        void remove(String name){
            tasks.remove(name);
        }

        Task fetch(String name){
            return tasks.get(name);
        }

        void complete(String name){
            for(Task task: tasks.values()){
                if(task.name.equalsIgnoreCase(name)){
                    task.completed = true;
                    break;
                }
            }
        }

        List<Task> list(){
            return new LinkedList<>(tasks.values());
        }
    }
}
